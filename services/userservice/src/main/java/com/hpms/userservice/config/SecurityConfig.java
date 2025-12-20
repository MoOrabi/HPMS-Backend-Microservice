package com.hpms.userservice.config;

import com.hpms.userservice.config.oauth2.CustomOAuth2UserService;
import com.hpms.userservice.config.oauth2.OAuth2AuthenticationFailureHandler;
import com.hpms.userservice.config.oauth2.OAuth2LoginSuccessHandler;
import com.hpms.userservice.filter.JwtAuthenticationFilter;
import com.hpms.userservice.repository.UserRepository;
import com.hpms.userservice.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@EnableWebSecurity
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.client-url}")
    private String clientUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://127.0.0.1:5500", clientUrl));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // cors configuration
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests((requests) ->
                requests.requestMatchers("/api/users/auth/**", "/api/users/mail/**",
                                "/api/users/oauth2/**",
                                "/api/users/auth/confirm",
                                "/api/users/auth/register",
                                "/api/users/company-profile/company-is-mobile-exist",
                                "/api/users/APIs/countryCodes",
                                "/api/users/APIs/jobNames",
                                "/v2/api-docs",
                                "/v3/api-docs/**",
                                "/configuration/ui",
                                "/swagger-resources/**",
                                "/configuration/security",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/swagger-ui/**",
                                "/index.html")
                        .permitAll()
                        .anyRequest().permitAll());
        http.addFilterAfter(new JwtAuthenticationFilter(jwtTokenUtils, userDetailsService), BasicAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
        http.oauth2Login(oAuth -> oAuth
                .authorizationEndpoint(entry -> entry.baseUri("/oauth2/authorize"))
                .redirectionEndpoint(entry -> entry.baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new SecurityUserDetailsService(userRepository);
    }

    protected void configure(AuthenticationManagerBuilder auth) {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        return new AuthenticationProviderService();
    }

}
