package pjserrano.authmicroservices.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pjserrano.authmicroservices.model.Credentials;
import pjserrano.authmicroservices.model.MyUser;
import pjserrano.authmicroservices.service.IAuthService;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final IAuthService authService;

    @Value("${expirationtime}")
    private long EXPIRATION_TIME;

    @Value("${key}")
    private String key;

    @Autowired
    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> register(@RequestBody MyUser user) {
        return authService.register(user)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.CREATED)));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<ResponseEntity<String>> login(@RequestBody Credentials credentials) {
        /* Si el usuario es válido, genera un token con su información y se la envía al cliente
        para que la utilice en las llamadas a los recursos */
        return authService.authenticateUser(credentials) // Delegar al servicio
                .map(details -> new ResponseEntity<>(getToken(details), HttpStatus.OK))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()))
                .onErrorResume(ResponseStatusException.class, e -> { // Manejo de errores más explícito
                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getReason()));
                    }
                    return Mono.error(e); // Re-lanzar otras excepciones
                });
    }

    private String getToken(UserDetails details) {
        return Jwts.builder()
                .subject(details.getUsername())
                .issuedAt(new Date())
                .claim("authorities", details.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(key.getBytes()), Jwts.SIG.HS512) // Usar jwtSecret
                .compact();
    }
}
