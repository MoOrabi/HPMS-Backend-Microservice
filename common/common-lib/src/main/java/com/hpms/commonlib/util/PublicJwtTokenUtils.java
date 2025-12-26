package com.hpms.commonlib.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.UUID;
import java.util.function.Function;

@Service
public class PublicJwtTokenUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
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
    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public UUID extractUUID(String token) {
        return UUID.fromString(extractClaim(token, Claims::getId));
    }
}
