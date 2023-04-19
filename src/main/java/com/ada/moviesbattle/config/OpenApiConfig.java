package com.ada.moviesbattle.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "moviesbattle_auth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customSwaggerConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movies Battle")
                        .description("This is a sample card game API Server. The main rule of our game consists in to provide the user two options of differente movie titles and they must guess which one has the best rating on [IMDb](https://www.omdbapi.com/).")
                        .version("1.0.0")
                        .contact(new Contact().email("italo.costa.ce@gmail.com").name("Italo costa"))
                );
    }

}
