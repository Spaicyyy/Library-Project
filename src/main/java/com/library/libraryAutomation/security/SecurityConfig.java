package com.library.libraryAutomation.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // 👈 ВАЖНО
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("*"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
// 🔓 ОТКРЫВАЕМ ДОСТУП К ФАЙЛАМ САЙТА
                                .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/index.html")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users.html")).permitAll() // 👈 ДОБАВИЛИ ЭТО!
                                .requestMatchers(new AntPathRequestMatcher("/style.css")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/script.js")).permitAll()

                                // 🔓 ЛОГИН ТОЖЕ ОТКРЫТ
                                .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()

                                // 🔒 ВСЕ ОСТАЛЬНЫЕ ЗАПРОСЫ (К ДАННЫМ) - ТОЛЬКО С ТОКЕНОМ
                                .anyRequest().authenticated()
                )//Sadece tokenle buralara izin verir
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } //Sifre hashing

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    } //Gelecekte giris parametrekerini degismek icin burda
}