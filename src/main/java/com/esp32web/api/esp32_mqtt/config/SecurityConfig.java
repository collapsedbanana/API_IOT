package com.esp32web.api.esp32_mqtt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // Endpoint de login ouvert à tous
                .requestMatchers("/api/auth/login").permitAll()
                // Endpoints de gestion des utilisateurs (création, modification) réservés aux ADMIN
                .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                
                // --- Exemples de nouvelles règles ---
                // Remplacez /api/capteurs/... par /api/devices/... ou /api/measurements/... 
                // selon votre nouveau design (Device + Measurement).
                .requestMatchers("/api/devices/**").hasRole("ADMIN")        // Ex: gestion des devices réservée aux admins
                .requestMatchers("/api/measurements/**").authenticated()   // Ex: accès aux mesures pour les utilisateurs connectés
                
                // Toute autre requête doit être authentifiée
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic()  // Pour faciliter les tests (en production, préférez JWT)
            .and()
            .formLogin().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
