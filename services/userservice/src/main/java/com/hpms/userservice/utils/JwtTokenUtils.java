package com.hpms.userservice.utils;

import com.hpms.userservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.hpms.userservice.utils.AppConstants.ACCESS_EXPATRIATION_TIME;
import static com.hpms.userservice.utils.AppConstants.REFRESH_EXPATRIATION_TIME;


@Service
public class JwtTokenUtils {

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }
    public UUID extractUUID(String token) {
        return UUID.fromString(extractClaim(token, Claims::getId));
    }

    @Value("${jwt.secret}")
    private String jwtSecret;


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

    public String generateRefreshToken(User user, Map<String, Object> extraClaims) {
        return generateToken(user, extraClaims, REFRESH_EXPATRIATION_TIME);
    }

    public String generateAccessToken(User userDetails, Map<String, Object> extraClaims) {
        return generateToken(userDetails, extraClaims, ACCESS_EXPATRIATION_TIME);
    }

    public String generateRefreshToken(User user) {
        return generateRefreshToken(user, generateExtraClaims((user)));
    }

    public String generateAccessToken(User user) {
        return generateAccessToken(user, generateExtraClaims(user));
    }

    public Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authority", user.getRole());
        return claims;
    }

    private String generateToken(User user, Map<String, Object> extraClaims, int exp) {
        return Jwts.builder()
                .claims(extraClaims)
                .id(user.getId().toString())
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(getSignInKey())
                .compact();
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && ! isTokenExpired(token));
    }


}
