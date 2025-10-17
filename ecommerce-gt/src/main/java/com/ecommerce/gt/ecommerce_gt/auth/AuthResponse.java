package com.ecommerce.gt.ecommerce_gt.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Respuesta devuelta tras el inicio de sesión exitoso.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String nombre;
    private String rolCodigo;
}