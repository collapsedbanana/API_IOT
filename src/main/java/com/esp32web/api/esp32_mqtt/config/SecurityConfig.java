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
                // Endpoints d'authentification ouverts
                .requestMatchers("/api/auth/**").permitAll()
                // Endpoints réservés aux administrateurs
                .requestMatchers("/api/capteurs/all", "/api/capteurs/assign", "/api/capteurs/delete/**").hasRole("ADMIN")
                // Par contre, l'endpoint pour voir ses propres capteurs est accessible à tout utilisateur authentifié
                .requestMatchers("/api/capteurs/mine").authenticated()
                // Toute autre requête doit être authentifiée
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic()  // Active Basic Auth pour faciliter les tests
            .and()
            .formLogin().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
