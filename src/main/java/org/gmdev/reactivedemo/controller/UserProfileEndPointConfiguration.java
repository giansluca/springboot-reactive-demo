package org.gmdev.reactivedemo.controller;

import org.gmdev.reactivedemo.config.CaseInsensitiveRequestPredicate;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.*;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserProfileEndPointConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(UserProfileHandler handler) {
        return route(i(GET("/user-profiles")), handler::all)
                .andRoute(i(GET("/user-profiles/{id}")), handler::getById)
                .andRoute(i(DELETE("/user-profiles/{id}")), handler::deleteById)
                .andRoute(i(POST("/user-profiles")), handler::create)
                .andRoute(i(PUT("/user-profiles/{id}")), handler::updateById);
    }

    private static RequestPredicate i(RequestPredicate target) {
        return new CaseInsensitiveRequestPredicate(target);
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
