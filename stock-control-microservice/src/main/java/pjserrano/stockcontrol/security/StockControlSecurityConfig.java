package pjserrano.stockcontrol.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.stereotype.Component;
import pjserrano.common.model.RoleType;
import pjserrano.commonsecurity.security.AuthManager;
import pjserrano.commonsecurity.security.CommonSecurityConfig;
import pjserrano.commonsecurity.security.SecurityContextRepository;

/*
Extendemos de la clase común definida en el modulo common-security para aprovechar las configuraciones comunes a
todos los microservicios, e implementamos la securización específica para los microservicios de stock-control-microservice
 */
@Component
@EnableWebFluxSecurity
public class StockControlSecurityConfig extends CommonSecurityConfig {

    @Autowired
    public StockControlSecurityConfig(AuthManager authManager, SecurityContextRepository securityContextRepository) {
        super(authManager, securityContextRepository);
    }

    @Override
    protected void authorizeExchange(AuthorizeExchangeSpec authorizeExchange) {
        /*authorizeExchange.pathMatchers("/products").permitAll()
                .pathMatchers("/products/by-category").permitAll()
                .pathMatchers("/product").permitAll()
                .pathMatchers("/add").hasAnyRole(RoleType.ROLE_ADMIN.getName(), RoleType.ROLE_OPERATOR.getName())
                .pathMatchers("/delete").hasAnyRole(RoleType.ROLE_ADMIN.getName(), RoleType.ROLE_OPERATOR.getName())
                .pathMatchers("/update/price").hasAnyRole(RoleType.ROLE_ADMIN.getName(), RoleType.ROLE_OPERATOR.getName())
                .pathMatchers("/update/add-stock").hasAnyRole(RoleType.ROLE_ADMIN.getName(), RoleType.ROLE_OPERATOR.getName())
                .pathMatchers("/update/subtract-stock").hasAnyRole(RoleType.ROLE_ADMIN.getName(), RoleType.ROLE_OPERATOR.getName())
                .anyExchange().authenticated();*/
        authorizeExchange.anyExchange().permitAll();
    }
}
