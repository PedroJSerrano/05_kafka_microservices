package pjserrano.stockcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories
@SpringBootApplication
public class StockControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockControlApplication.class, args);
    }

}
