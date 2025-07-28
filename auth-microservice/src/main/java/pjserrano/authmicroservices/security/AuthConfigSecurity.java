package pjserrano.authmicroservices.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.stereotype.Component;
import pjserrano.commonsecurity.security.AuthManager;
import pjserrano.commonsecurity.security.CommonSecurityConfig;
import pjserrano.commonsecurity.security.SecurityContextRepository;

/*
Extendemos de la clase común definida en el modulo common-security para aprovechar las configuraciones comunes a
todos los microservicios, e implementamos la securización específica para los microservicios de auth-microservice
 */
@Component
@EnableWebFluxSecurity
public class AuthConfigSecurity extends CommonSecurityConfig {

    @Autowired
    public AuthConfigSecurity(AuthManager authManager, SecurityContextRepository securityContextRepository) {
        super(authManager, securityContextRepository);
    }

    @Override
    protected void authorizeExchange(AuthorizeExchangeSpec authorizeExchange) {
        authorizeExchange.pathMatchers("/auth/login").permitAll()
                .pathMatchers("/auth/register").permitAll()
                .anyExchange().authenticated();
    }
}
