package ru.skypro.stock_of_socks;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class StockOfSocksApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockOfSocksApplication.class, args);
    }

}
