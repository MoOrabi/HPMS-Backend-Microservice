package com.hpms.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("users_path", r -> r.path("/api/users/**")
						.uri("lb://USER-SERVICE"))
				.route("jobs_path", r -> r.path("/api/jobs/**")
						.uri("lb://JOB-SERVICE"))
				.route("applications_path", r -> r.path("/api/apps/**")
						.uri("lb://APPLICATIONS-SERVICE"))
//				.route("host_route", r -> r.host("*.myhost.org")
//						.uri("https://httpbin.org"))
//				.route("rewrite_route", r -> r.host("*.rewrite.org")
//						.filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
//						.uri("https://httpbin.org"))
//				.route("circuit_breaker_route", r -> r.host("*.circuitbreaker.org")
//						.filters(f -> f.circuitBreaker(c -> c.setName("slowcmd")))
//						.uri("https://httpbin.org"))
//				.route("circuit_breaker_fallback_route", r -> r.host("*.circuitbreakerfallback.org")
//						.filters(f -> f.circuitBreaker(c -> c.setName("slowcmd").setFallbackUri("forward:/circuitbrekerfallback")))
//						.uri("https://httpbin.org"))
//				.route("limit_route", r -> r
//						.host("*.limited.org").and().path("/anything/**")
//						.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
//						.uri("https://httpbin.org"))
				.build();
	}

}
