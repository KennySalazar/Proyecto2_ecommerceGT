package com.ecommerce.gt.ecommerce_gt.auth.dto;

import lombok.Data;

/** Petición de inicio de sesión. */
@Data
public class LoginRequest {
  private String correo;
  private String password;
}