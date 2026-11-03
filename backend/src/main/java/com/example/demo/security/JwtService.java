package com.example.demo.security;

import jakarta.annotation.PostConstruct;

import com.example.demo.entity.SystemUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private long refreshTokenExpiration;

    @PostConstruct
    public void init() {

    refreshTokenExpiration = accessTokenExpiration * 7;

    }

    public String generateAccessToken(SystemUser user) {

        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("role", user.getRole().name());
        extraClaims.put("userId", user.getId());
        extraClaims.put("fullName", user.getFullName());

        return buildToken(
                extraClaims,
                user.getUsername(),
                accessTokenExpiration
        );
    }

    public String generateRefreshToken(SystemUser user) {

        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("role", user.getRole().name());
        extraClaims.put("tokenType", "REFRESH");

        return buildToken(
                extraClaims,
                user.getUsername(),
                refreshTokenExpiration
        );
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String subject,
            long expiration
    ) {

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(subject)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
    }

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);

    }

    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);

    }

    public boolean isTokenValid(
            String token,
            String username
    ) {

        return extractUsername(token).equals(username)
                && !isTokenExpired(token);

    }

    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());

    }

    private Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );

    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);

    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);

    }

}