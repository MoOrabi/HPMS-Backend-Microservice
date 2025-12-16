package com.hpms.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;


@Component
@Slf4j
public class AuthenticationFilter implements GatewayFilter {

    @Value("jwt.secret")
    private String jwtSecret;
    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        String token = extractToken(exchange.getRequest());

        if (token == null || !isTokenExpired(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Add user info to headers for downstream services
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", extractId(token))
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    private String extractToken(ServerHttpRequest request) {
        final String authHeader = request.getHeaders().getFirst("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            jwt = authHeader.substring(7);
            return jwt;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())  // ✅ New method
                    .build()
                    .parseSignedClaims(token)    // ✅ New method
                    .getPayload();               // ✅ getPayload() instead of getBody()
        } catch (Exception ex) {
            throw new RuntimeException("Invalid JWT token: " + ex.getMessage(), ex);
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
