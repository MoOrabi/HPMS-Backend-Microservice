package com.hpms.jobservice.config;

import com.hpms.commonlib.handler.BadRequestException;
import com.hpms.commonlib.handler.ResourceNotFoundException;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.jobservice.service.client.AppServiceClient;
import com.hpms.jobservice.service.client.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class HttpClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .filter(handleErrors());
    }

    @Bean
    public UserServiceClient userServiceClient(WebClient.Builder builder) {
        WebClient webClient = builder
                .baseUrl("http://user-service")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(UserServiceClient.class);
    }

    @Bean
    public AppServiceClient jobServiceClient(WebClient.Builder builder) {
        WebClient webClient = builder
                .baseUrl("http://application-service")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(AppServiceClient.class);
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Calling service: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction handleErrors() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("No response body")
                        .flatMap(errorBody -> {
                            log.error("Service error response: {}", errorBody);

                            // Extract service name from URL
                            String serviceName = extractServiceName(clientResponse);

                            // Map different status codes to appropriate exceptions
                            if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                                return Mono.error(new ResourceNotFoundException(
                                        "Resource not found in " + serviceName + ": " + errorBody
                                ));
                            } else if (clientResponse.statusCode() == HttpStatus.BAD_REQUEST) {
                                return Mono.error(new BadRequestException(
                                        "Bad request to " + serviceName + ": " + errorBody
                                ));
                            } else if (clientResponse.statusCode().is5xxServerError()) {
                                return Mono.error(new ServiceCommunicationException(
                                        serviceName,
                                        clientResponse.statusCode(),
                                        errorBody
                                ));
                            } else {
                                return Mono.error(new ServiceCommunicationException(
                                        serviceName,
                                        clientResponse.statusCode(),
                                        errorBody
                                ));
                            }
                        });
            }
            return Mono.just(clientResponse);
        });
    }

    private String extractServiceName(ClientResponse response) {
        String url = response.request().getURI().toString();
        if (url.contains("job-service")) return "job-service";
        if (url.contains("user-service")) return "user-service";
        return "unknown-service";
    }
}
