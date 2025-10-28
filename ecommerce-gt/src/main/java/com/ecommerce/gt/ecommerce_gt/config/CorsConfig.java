package com.ecommerce.gt.ecommerce_gt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

/**
 * CONFIGURACIÓN GLOBAL DE CORS (Cross-Origin Resource Sharing).
 * PERMITE QUE EL FRONTEND SE COMUNIQUE CON EL BACKEND.
 */
@Configuration
public class CorsConfig {

  /**
   * DEFINE UN FILTRO CORS PERSONALIZADO PARA ACEPTAR PETICIONES DESDE ORÍGENES
   * PERMITIDOS.
   * SE PERMITEN MÉTODOS Y CABECERAS BÁSICAS, Y SE HABILITAN CREDENCIALES
   *
   * @return INSTANCIA DE CorsFilter CONFIGURADA
   */
  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();

    // LISTA DE ORÍGENES PERMITIDOS
    config.setAllowedOriginPatterns(List.of(
        "http://localhost:4200", // FRONTEND LOCAL
        "https://*.netlify.app", // NETLIFY
        "https://stellar-cajeta-18e439.netlify.app", // FRONTEND ESPECÍFICO
        "https://*.ngrok-free.dev" // TÚNELES NGROK PARA BACKEND
    ));

    // MÉTODOS HTTP PERMITIDOS
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

    // CABECERAS PERMITIDAS EN LAS PETICIONES
    config.setAllowedHeaders(List.of("*"));

    // PERMITE EL ENVÍO DE COOKIES Y HEADERS AUTORIZADOS
    config.setAllowCredentials(true);

    // TIEMPO DE CACHE DE LA CONFIGURACIÓN CORS (EN SEGUNDOS)
    config.setMaxAge(3600L);

    // REGISTRA LA CONFIGURACIÓN PARA TODAS LAS RUTAS
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}
