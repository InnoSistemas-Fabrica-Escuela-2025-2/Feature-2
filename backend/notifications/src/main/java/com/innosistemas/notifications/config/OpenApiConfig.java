package com.innosistemas.notifications.config;

import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Notifications Service API")
                .version("1.0")
                .description("API documentation for the Notifications Service"));
    }
}
