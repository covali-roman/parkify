package com.covaliroman.parkify.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String FACILITIES_TAG = "Parking facilities";
    public static final String LEVELS_TAG = "Parking levels";
    public static final String SPACES_TAG = "Parking spaces";

    @Bean
    public OpenAPI parkifyOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Parkify API")
                        .description("REST API for managing parking facilities, levels, and spaces.")
                        .version("v1"))
                .tags(List.of(
                        new Tag()
                                .name(FACILITIES_TAG)
                                .description("Create, retrieve, update, and activate or deactivate parking facilities."),
                        new Tag()
                                .name(LEVELS_TAG)
                                .description("Manage the levels that belong to a parking facility."),
                        new Tag()
                                .name(SPACES_TAG)
                                .description("Manage parking spaces and their availability characteristics.")
                ));
    }
}
