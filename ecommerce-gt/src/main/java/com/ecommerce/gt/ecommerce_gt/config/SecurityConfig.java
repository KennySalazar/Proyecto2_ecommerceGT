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
 * CONFIGURACIÓN PRINCIPAL DE SPRING SECURITY CON JWT.
 * - DESACTIVA CSRF (APIS SIN SESIONES).
 * - HABILITA CORS (SE CONFIGURA EN CorsConfig).
 * - DEFINE QUÉ RUTAS SON PÚBLICAS Y CUÁLES REQUIEREN ROLES.
 * - AGREGA EL FILTRO JWT ANTES DEL USERNAME/PASSWORD FILTER.
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    /** FILTRO QUE VALIDA EL TOKEN JWT Y ESTABLECE LA AUTENTICACIÓN. */
    private final JwtFiltro jwtFiltro;

    /**
     * CADENA DE FILTROS DE SEGURIDAD.
     * AQUÍ SE ESPECIFICA EL ACCESO A ENDPOINTS Y SE INYECTA EL FILTRO JWT.
     *
     * @param http OBJETO DE CONFIGURACIÓN HTTP SECURITY.
     * @return SecurityFilterChain CONFIGURADA.
     * @throws Exception SI FALLA LA CONSTRUCCIÓN DE LA CADENA.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF DESACTIVADO PARA API REST.
                .csrf(csrf -> csrf.disable())
                // CORS HABILITADO
                .cors(c -> {
                })
                // AUTORIZACIÓN DE PETICIONES POR RUTAS Y MÉTODOS.
                .authorizeHttpRequests(auth -> auth
                        // PRE-FLIGHT CORS: SIEMPRE PERMITIDO.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ENDPOINTS PÚBLICOS (SIN TOKEN).
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/catalogos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/publico").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                        // RUTAS DE LOGÍSTICA: REQUIERE ROL LOGISTICA.
                        .requestMatchers(HttpMethod.GET, "/api/logistica/**").hasRole("LOGISTICA")
                        .requestMatchers(HttpMethod.PUT, "/api/logistica/**").hasRole("LOGISTICA")
                        .requestMatchers(HttpMethod.POST, "/api/logistica/**").hasRole("LOGISTICA")

                        // CARRITO: CHECKOUT REQUIERE AUTENTICACIÓN; RESTO REQUIERE ROL COMUN.
                        .requestMatchers(HttpMethod.POST, "/api/carrito/checkout").authenticated()
                        .requestMatchers("/api/carrito/**").hasRole("COMUN")

                        // RUTAS DEL USUARIO COMÚN.
                        .requestMatchers("/api/productos/mis/**").hasRole("COMUN")
                        .requestMatchers("/api/pedidos/**").hasRole("COMUN")
                        .requestMatchers("/api/usuarios/mis/**").hasRole("COMUN")

                        .requestMatchers(HttpMethod.GET, "/api/productos/*/resenas/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/productos/*/resenas").hasRole("COMUN")

                        // MÓDULO MODERADOR.
                        .requestMatchers("/api/moderador/**").hasRole("MODERADOR")
                        // GANANCIAS (VENDEDOR/COMÚN) Y ADMIN.
                        .requestMatchers("/api/ganancias/**").hasRole("COMUN")
                        .requestMatchers("/api/admin/ganancias/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/reportes/**").hasRole("ADMIN")

                        // CUALQUIER OTRA RUTA REQUIERE AUTENTICACIÓN.
                        .anyRequest().authenticated())
                // INYECTA EL FILTRO JWT ANTES DEL FILTRO DE USUARIO/PASSWORD.
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
