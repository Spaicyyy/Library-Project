package com.library.libraryAutomation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter  { //Garanti veriyorki bir token sadece bir kere yoklanacak

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String authHeader = request.getHeader("Authorization");//burani ariyourz , cunki token bura geliyor

        if (authHeader != null && authHeader.startsWith("Bearer ")) { //Token Yokluyor
            String token = authHeader.substring(7); //ilk 7 harfi kesiyor , sadece token kaliyor

            if (jwtUtils.validateToken(token)) {
                String email = jwtUtils.getEmailFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);

                // Spring role yerini "ROLE_" seklinde bekliyor
                var authority = new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role);

                UserDetails userDetails = User.builder()
                        .username(email)
                        .password("")
                        .authorities(java.util.List.of(authority)) // Rolu'da izin yerine veriyoruz
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } //Tokenin zamani gecmemis ve gercek oldugunu soyluyor

        filterChain.doFilter(request, response); //Sonraki token sisteme giriyor
    }
}