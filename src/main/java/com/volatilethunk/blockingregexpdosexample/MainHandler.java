package com.volatilethunk.blockingregexpdosexample;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

import static java.lang.System.out;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class MainHandler {

    /**
     * Ensure outputs are displayed by flushing before blocking examples kick in.
     */
    private static void display(Object object) {
        out.println(object);
        out.flush();
    }

    /**
     * Do primitive benchmarking for the passed in code block.
     */
    private static <T> T benchmark(Supplier<T> supplier) {
        var stopWatch = new StopWatch();
        try {
            display("starting stopwatch");
            stopWatch.start();

            return supplier.get();
        } finally {
            stopWatch.stop();
            display(stopWatch);
        }
    }

    /**
     * Demonstrate a single request taking roughly half a minute to finish.
     */
    Mono<ServerResponse> index(ServerRequest request) {
        var name = request
                .queryParam("name")
                .orElseThrow();

        var escapedRegexp = request
                .queryParam("regexp")
                .orElseThrow();

        // This is a nasty hack to get around Spring's handling of `+`s in its UriBuilder.
        // See: https://github.com/spring-projects/spring-framework/issues/14464
        var regexp = escapedRegexp.replace(" ", "+");

        return benchmark(() -> {
            var matches = name.matches(regexp);
            return matches
                    ? ok().build()
                    : notFound().build();
        });
    }
}
