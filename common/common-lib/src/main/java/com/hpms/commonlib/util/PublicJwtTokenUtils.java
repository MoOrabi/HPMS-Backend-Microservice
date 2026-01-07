package com.hpms.commonlib.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.UUID;
import java.util.function.Function;

public class PublicJwtTokenUtils {

//    @Value("${jwt.secret}")
//    private String jwtSecreta;

    private static SecretKey getSignInKey() {
        String jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
                System.getenv("JWT_SECRET");

        // Fallback to system property if env var not found
        if (jwtSecret == null) {
            jwtSecret = System.getProperty("jwt.secret");
        }

        // Last resort: throw exception
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT secret not configured. Set JWT_SECRET environment variable or jwt.secret system property.");
        }
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
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
    public static String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public static UUID extractUUID(String token) {
        return UUID.fromString(extractClaim(token, Claims::getId));
    }
}
