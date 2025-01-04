package site.fitmon.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
    servers = {
      @Server(url = "https://api.fitmon.site", description = "개발 서버"),
      @Server(url = "http://localhost:8080", description = "로컬 서버")
    })
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI crewCrewApi() {
    Info info = new Info().version("1.0.0").title("Fitmon API").description("Fitmon API 명세서");

    io.swagger.v3.oas.models.security.SecurityScheme securityScheme =
        new io.swagger.v3.oas.models.security.SecurityScheme()
            .name(HttpHeaders.AUTHORIZATION)
            .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
            .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
            .bearerFormat("Authorization")
            .scheme("bearer");
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT");

    return new OpenAPI()
        .info(info)
        .addSecurityItem(securityRequirement)
        .components(new Components().addSecuritySchemes("JWT", securityScheme));
  }
}
