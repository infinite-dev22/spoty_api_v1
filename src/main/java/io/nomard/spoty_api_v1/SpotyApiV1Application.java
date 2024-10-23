package io.nomard.spoty_api_v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass=true)
@EnableCaching
public class SpotyApiV1Application {

    public static void main(String[] args) {
        SpringApplication.run(SpotyApiV1Application.class, args);
    }

}
