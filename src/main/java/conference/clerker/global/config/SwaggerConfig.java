package conference.clerker.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Clerker",
                description = "D & X : W Conference Project - 회의 지원 플랫폼",
                version = "v1"
        ),
        servers = {
            @Server(url="http://localhost:8080", description = "로컬 서버")
        }
)

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT");
        Components components = new Components().addSecuritySchemes("JWT", new SecurityScheme()
                .name("JWT")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );
        return new OpenAPI()
                .components(new Components())
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
