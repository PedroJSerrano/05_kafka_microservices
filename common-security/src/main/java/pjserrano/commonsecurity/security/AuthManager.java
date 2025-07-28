package pjserrano.commonsecurity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication; // Asegúrate de esta importación
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthManager implements ReactiveAuthenticationManager {

    @Value("${key}")
    private String key;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();

        return Mono.just(authToken)
                .map(token -> {
                    Claims claims = Jwts.parser()
                            .verifyWith(Keys.hmacShaKeyFor(key.getBytes()))
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String authoritiesString = claims.get("authorities", String.class);
                    List<GrantedAuthority> authorities = Arrays.stream(authoritiesString.split(","))
                            .filter(s -> !s.trim().isEmpty())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    // Esto devuelve un UsernamePasswordAuthenticationToken que es un tipo de Authentication
                    return (Authentication) new UsernamePasswordAuthenticationToken( // <-- ¡CAST explícito aquí!
                            claims.getSubject(),
                            null,
                            authorities
                    );
                })
                .onErrorResume(ExpiredJwtException.class, e -> {
                    System.err.println("ERROR (AuthManager): JWT expired: " + e.getMessage());
                    // Asegurarse de que el Mono devuelto sea del tipo Mono<Authentication>
                    return Mono.error(new BadCredentialsException("JWT expired", e));
                })
                .onErrorResume(SignatureException.class, e -> {
                    System.err.println("ERROR (AuthManager): Invalid JWT signature: " + e.getMessage());
                    // Asegurarse de que el Mono devuelto sea del tipo Mono<Authentication>
                    return Mono.error(new BadCredentialsException("Invalid JWT signature", e));
                })
                .onErrorResume(MalformedJwtException.class, e -> {
                    System.err.println("ERROR (AuthManager): Malformed JWT: " + e.getMessage());
                    // Asegurarse de que el Mono devuelto sea del tipo Mono<Authentication>
                    return Mono.error(new BadCredentialsException("Malformed JWT", e));
                })
                .onErrorResume(Throwable.class, e -> {
                    System.err.println("ERROR (AuthManager): Unexpected error during JWT authentication: " + e.getMessage());
                    // Asegurarse de que el Mono devuelto sea del tipo Mono<Authentication>
                    return Mono.error(new BadCredentialsException("Invalid or unexpected JWT authentication error", e));
                });
    }
}