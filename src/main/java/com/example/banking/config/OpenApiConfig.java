package com.example.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenApiCustomizer customOpenApi() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    ApiResponses responses = operation.getResponses();
                    if (responses.containsKey("400")) {
                        ApiResponse response = responses.get("400");
                        response.setDescription("Bad Request");
                    }
                    if (responses.containsKey("404")) {
                        ApiResponse response = responses.get("404");
                        response.setDescription("Not Found");
                    }
                });
            });
        };
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Banking Transactions API")
                        .version("1.0.0")
                        .description("API for managing accounts and transactions"));
    }
}