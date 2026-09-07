package it.its.cinema.shows_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI showsOpenApi(){
        return new OpenAPI().info(new Info()
                .title("shows-service")
                .version("1.0.0")
                .description("Catalogo film e spettacoli, disponibilità dei posti.")
        );
    }
}
