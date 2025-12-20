package com.hpms.userservice.config.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.client-url}")
    private String clientUrl;

    @Override
    public void onAuthenticationFailure(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, AuthenticationException exception) throws IOException {
        System.out.println("On Failure");
        var targetUrl = UriComponentsBuilder.fromUriString(clientUrl+"/auth/social/")
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();
        System.out.println(exception.getLocalizedMessage());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
