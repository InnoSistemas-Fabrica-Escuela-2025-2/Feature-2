package com.innosistemas.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routerBuilder(RouteLocatorBuilder routeLocaterBuilder){
		return routeLocaterBuilder.routes()
		.route("authenticator", r->r.path("/authenticator/**")
			.uri("http://localhost:8081/"))
		.route("innosistemas",r->r.path("/project/**")
			.uri("http://localhost:8082/")).build();
	}
}
