package conference.clerker.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT");

        Components components = new Components()
                .addSecuritySchemes("JWT", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                );

        Server localServer = new Server()
                .url("http://127.0.0.1:8080")
                .description("로컬 서버");

        Server deployServer = new Server()
                .url(serverUrl)
                .description("배포 서버");

        return new OpenAPI()
                .info(new Info()
                        .title("Clerker")
                        .description("D & X : W Conference Project - 회의 지원 플랫폼")
                        .version("v1"))
                .addServersItem(localServer)
                .addServersItem(deployServer)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}