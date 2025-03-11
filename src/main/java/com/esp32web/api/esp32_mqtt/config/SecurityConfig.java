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
            // Désactive la protection CSRF (pour faciliter les appels depuis un front end externe)
            .csrf().disable()

            // Configuration des règles d'autorisation
            .authorizeHttpRequests(auth -> auth
                // Autorise l'accès sans authentification à /api/auth/** (login, register)
                .requestMatchers("/api/auth/**").permitAll()

                // Réserve l'accès à /api/admin/users/** aux administrateurs
                .requestMatchers("/api/admin/users/**").hasRole("ADMIN")

                // Toute autre requête doit être authentifiée
                .anyRequest().authenticated()
            )

            // Utilise un mode "STATELESS" : pas de session côté serveur
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Active l'authentification HTTP Basic (popup dans le navigateur)
            .httpBasic()
            .and()

            // Désactive le formulaire de login par défaut
            .formLogin().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Utilise BCrypt pour hasher les mots de passe
        return new BCryptPasswordEncoder();
    }
}
