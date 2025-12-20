package com.library.libraryAutomation.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final String SECRET = "MySuperSecretKeyForLibraryAutomationProject1234567890"; //Tokenler yapmak icin gizli kod
    private static final long EXPIRATION_TIME = 86400000; //Bir token 24 saat sure bilir

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes()); //gizli kodu kullankmak icin bytlara ceviriyoruz

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();  //Tum bilgileri bura yaziyor
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("Wrong token: " + e.getMessage());
        }
        return false;
    } //Token yoklanisi , gercek token mi yoksa yok
}