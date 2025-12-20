package com.library.libraryAutomation.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // СЕКРЕТНЫЙ КЛЮЧ (В реальном проекте прячем в настройки, для учебы оставим тут)
    // Должен быть длинным и сложным!
    private static final String SECRET = "MySuperSecretKeyForLibraryAutomationProject1234567890";
    private static final long EXPIRATION_TIME = 86400000; // 24 часа

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 1. Создать токен (Выдать пропуск)
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // Зашиваем роль внутрь токена
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Получить Email из токена
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 3. Проверить валидность токена
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("Неверный токен: " + e.getMessage());
        }
        return false;
    }
}