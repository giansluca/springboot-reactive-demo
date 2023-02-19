package org.gmdev.reactivedemo.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserProfileEndPointConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(UserProfileHandler handler) {
        return route(GET("/user-profiles"), handler::all)
                .andRoute(GET("/user-profiles/{id}"), handler::getById)
                .andRoute(DELETE("/user-profiles/{id}"), handler::deleteById)
                .andRoute(POST("/user-profiles"), handler::create)
                .andRoute(PUT("/user-profiles/{id}"), handler::updateById);
    }

    @Bean
    CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

}
