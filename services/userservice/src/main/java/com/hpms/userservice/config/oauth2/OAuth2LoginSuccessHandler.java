package com.hpms.userservice.config.oauth2;

import com.hpms.userservice.config.AppProperties;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.model.User;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.UserRepository;
import com.hpms.userservice.utils.CookieUtils;
import com.hpms.userservice.utils.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;


@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private JwtTokenUtils jwtTokenUtils;

    private UserRepository userRepository;

    private JobSeekerProfileRepository jobSeekerProfileRepository;

    private AppProperties appProperties;

    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    public OAuth2LoginSuccessHandler(JwtTokenUtils jwtTokenUtils, UserRepository userRepository,
                                     JobSeekerProfileRepository jobSeekerProfileRepository,
                                     AppProperties appProperties,
                                     HttpCookieOAuth2AuthorizationRequestRepository
                                             httpCookieOAuth2AuthorizationRequestRepository) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.appProperties = appProperties;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Value("${app.client-url}")
    private String clientUrl;

    public OAuth2LoginSuccessHandler(String defaultTargetUrl, JwtTokenUtils jwtTokenUtils, UserRepository userRepository, JobSeekerProfileRepository jobSeekerProfileRepository) {
        super(defaultTargetUrl);
        this.jwtTokenUtils = jwtTokenUtils;
        this.userRepository = userRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        System.out.println("OnSuccess");
        String targetUrl = determineTargetUrl(request, response, authentication);
        User user = new User();
        user.setUsername(authentication.getName());
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws RuntimeException {
        Optional<String> redirectUri =
                CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the " +
                    "authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        Optional<JobSeeker> user = jobSeekerProfileRepository.getByUsername(authentication.getName());

        Map<String, Object> claims = jwtTokenUtils.generateExtraClaims(user.get());

        String accessToken = jwtTokenUtils.generateAccessToken(user.get(), claims);
        String refreshToken = jwtTokenUtils.generateRefreshToken(user.get(), claims);
        System.out.println("--------------- Target Url " + targetUrl);
        return UriComponentsBuilder.fromUriString(clientUrl+"/auth/login/job-seeker")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris().stream().anyMatch(authorizedRedirectUri -> {
            // Only validate host and port. Let the clients use different paths if they want
            // to
            URI authorizedURI = URI.create(authorizedRedirectUri);
            return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort();
        });
    }
}