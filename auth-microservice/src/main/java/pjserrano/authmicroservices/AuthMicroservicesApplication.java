package pjserrano.authmicroservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
@ComponentScan(basePackages = {"pjserrano.authmicroservices", "pjserrano.commonsecurity"})
public class AuthMicroservicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthMicroservicesApplication.class, args);
    }

}
