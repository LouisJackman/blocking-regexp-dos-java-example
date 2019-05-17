package com.volatilethunk.blockingregexpdosexample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class MainRouter {

    @Bean
    public RouterFunction<ServerResponse> router(MainHandler handler) {
        return RouterFunctions.route(GET("/"), handler::index);
    }
}
