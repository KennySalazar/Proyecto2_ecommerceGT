package com.ecommerce.gt.ecommerce_gt.config;

import com.ecommerce.gt.ecommerce_gt.seguridad.JwtFiltro;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

/**
 * Configuración principal de Spring Security para JWT.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFiltro jwtFiltro;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(c -> {
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/catalogos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/publico").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/carrito/checkout").authenticated()
                        .requestMatchers("/api/carrito/**").hasRole("COMUN")

                        .requestMatchers("/api/productos/mis/**").hasRole("COMUN")
                        .requestMatchers("/api/pedidos/**").hasRole("COMUN")
                        .requestMatchers("/api/usuarios/mis/**").hasRole("COMUN")
                        .requestMatchers(HttpMethod.GET, "/api/productos/*/resenas/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/productos/*/resenas").hasRole("COMUN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}