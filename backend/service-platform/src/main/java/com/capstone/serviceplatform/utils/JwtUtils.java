package com.capstone.serviceplatform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String SECRET = "votreSecretTresLongAvecAuMoins256BitsPourHS256!1234567890";
    // Durée par défaut allongée : les sessions de test/utilisation réelles
    // s'étalent souvent sur plusieurs jours, et une expiration à 24h faisait
    // apparaître "Votre session a expiré" en pleine utilisation dès qu'une
    // requête (ex: laisser un avis) tombait après ce délai.
    private static final long EXPIRATION_MS = 259200000L; // 3 jours
    public static final long REMEMBER_ME_EXPIRATION_MS = 2592000000L; // 30 jours

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email, String role) {
        return generateToken(email, role, EXPIRATION_MS);
    }

    public String generateToken(String email, String role, long expirationMs) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}