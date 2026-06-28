package org.sopt.makers.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  private static final String BEARER_AUTH = "bearerAuth";

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("SOPT Makers API").version("v1"))
        .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
        .components(
            new Components()
                .addSecuritySchemes(
                    BEARER_AUTH,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  @Bean
  public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder()
        .group("Admin")
        .packagesToScan("org.sopt.makers.api.controller.admin")
        .build();
  }

  @Bean
  public GroupedOpenApi appApi() {
    return GroupedOpenApi.builder()
        .group("App")
        .packagesToScan("org.sopt.makers.api.controller.app")
        .build();
  }

  @Bean
  public GroupedOpenApi authApi() {
    return GroupedOpenApi.builder()
        .group("Auth")
        .packagesToScan("org.sopt.makers.api.controller.auth")
        .build();
  }

  @Bean
  public GroupedOpenApi officialApi() {
    return GroupedOpenApi.builder()
        .group("Official")
        .packagesToScan(
            "org.sopt.makers.api.controller.official", "org.sopt.makers.api.controller.internal")
        .build();
  }

  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder()
        .group("User")
        .packagesToScan("org.sopt.makers.api.controller.user")
        .build();
  }
}
