package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    private final ApplicationConfiguration config;

    public OpenApiConfig(final ApplicationConfiguration config) {
        this.config = config;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(config.getName())
                        .version("v0.0.1"));
    }

}
