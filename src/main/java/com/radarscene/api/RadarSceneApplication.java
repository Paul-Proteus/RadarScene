package com.radarscene.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the RadarScene application.
 * This Spring Boot application handles user authentication and scene generation
 * for hyper-niche interests based on user location and preferences.
 */
@SpringBootApplication
@EnableJpaAuditing
public class RadarSceneApplication {

    public static void main(String[] args) {
        SpringApplication.run(RadarSceneApplication.class, args);
    }
}
