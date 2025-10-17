package com.ecommerce.gt.ecommerce_gt.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Respuesta de login con JWT y datos mínimos del usuario. */
@Data
@AllArgsConstructor
public class LoginResponse {
  private String token;
  private String nombre;
  private String rolCodigo;
}