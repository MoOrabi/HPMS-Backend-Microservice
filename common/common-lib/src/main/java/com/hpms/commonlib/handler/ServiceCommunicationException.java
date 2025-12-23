package com.hpms.commonlib.handler;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ServiceCommunicationException extends RuntimeException {
    private final String serviceName;
    private final HttpStatusCode statusCode;
    private final String responseBody;

    public ServiceCommunicationException(String serviceName, HttpStatusCode statusCode, String responseBody) {
        super(String.format("Error calling %s service: %s - %s", serviceName, statusCode, responseBody));
        this.serviceName = serviceName;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ServiceCommunicationException(String serviceName, String message, Throwable cause) {
        super(String.format("Error calling %s service: %s", serviceName, message), cause);
        this.serviceName = serviceName;
        this.statusCode = null;
        this.responseBody = null;
    }

}
