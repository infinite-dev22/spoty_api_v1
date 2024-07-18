package io.nomard.spoty_api_v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class, SecurityAutoConfiguration.class})
//@SpringBootApplication
@EnableR2dbcRepositories
public class SpotyApiV1Application {
    public static void main(String[] args) {
        SpringApplication.run(SpotyApiV1Application.class, args);
    }
}
