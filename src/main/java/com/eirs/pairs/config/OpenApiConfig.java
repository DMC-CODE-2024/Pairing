package com.eirs.pairs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        OpenAPI openApi = new OpenAPI()
                .info(new Info().title("Pairing Service")
                        .description("Pairing Service")
                        .version("1.0"));
        return openApi;
    }
}