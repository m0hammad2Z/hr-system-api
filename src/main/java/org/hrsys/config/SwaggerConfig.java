package org.hrsys.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    GroupedOpenApi authenticatedApi() {
        return GroupedOpenApi.builder()
                .group("authenticated-api")
                .pathsToMatch("/login")
                .build();
    }

    @Bean
    OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(new io.swagger.v3.oas.models.info.Info().title("HR System API").version("1.0.0"));
        openAPI.addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearer-key"));
        openAPI.components(new Components()
                .addSecuritySchemes("bearer-key", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
        );
        return openAPI;
    }
}
