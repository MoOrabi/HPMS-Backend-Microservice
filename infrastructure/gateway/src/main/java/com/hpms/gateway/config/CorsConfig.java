package com.hpms.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // ✅ Allow specific origins (more secure than "*")
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",
                "http://127.0.0.1:4200",
                "http://127.0.0.1:3000",
                "http://localhost:4200/"
        ));

        // ✅ Allow all methods
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // ✅ Allow all headers
        corsConfig.setAllowedHeaders(List.of("*"));

        // ✅ Allow credentials (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);

        // ✅ Expose headers to frontend
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-User-Id",
                "Content-Type"
        ));

        // ✅ Max age for preflight cache
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}