package com.e_val.e_Val.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.e_val.e_Val.model.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtils {
    private static final String SECRET_KEY = "your_secret_key_here"; // Replace with your actual secret key
    private static final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24 hours
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        
        logger.info("Generating token for user: {}, role: {}", user.getEmail(), user.getRole().name());
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        logger.info("Generated token: {}", token.substring(0, 20) + "...");
        return token;
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        logger.info("Generating token for userDetails: {}", userDetails.getUsername());
        String token = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        logger.info("Generated token: {}", token.substring(0, 20) + "...");
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            if (token == null || token.isEmpty()) {
                logger.error("Token is null or empty");
                return false;
            }
            final String username = extractUsername(token);
            if (username == null) {
                logger.error("No username found in token");
                return false;
            }
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.info("Token validation for user: {}, valid: {}", username, isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            if (expiration == null) {
                logger.error("Expiration claim is missing in token");
                return true; // Treat missing expiration as expired
            }
            boolean expired = expiration.before(new Date());
            logger.info("Token expiration check: expired={}", expired);
            return expired;
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims == null) {
                logger.error("No claims extracted from token");
                return null;
            }
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            logger.error("Error extracting claim from token: {}", e.getMessage());
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {}", e.getMessage());
            return null;
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}