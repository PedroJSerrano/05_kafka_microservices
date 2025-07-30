package pjserrano.commonsecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Necesario para crear el token
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private AuthManager authManager;

    @Autowired
    public SecurityContextRepository(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty(); // No guardamos el contexto de seguridad en el lado del servidor para JWT
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .flatMap(authHeader -> {
                    String authToken = authHeader.substring(7); // Extrae solo el token JWT

                    // Pasa el token JWT al AuthManager para que lo valide y cree el objeto Authentication
                    // El principal y las credenciales son el token JWT completo.
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(authToken, authToken);

                    return this.authManager.authenticate(auth)
                            .map(SecurityContextImpl::new) // Si AuthManager autentica, crea el SecurityContext
                            .onErrorResume(e -> {
                                // Log de errores si AuthManager falla la autenticación del JWT
                                System.err.println("DEBUG (SecurityContextRepository): AuthManager failed to authenticate JWT: " + e.getMessage());
                                return Mono.empty(); // No se establece el contexto de seguridad
                            });
                });
    }
}