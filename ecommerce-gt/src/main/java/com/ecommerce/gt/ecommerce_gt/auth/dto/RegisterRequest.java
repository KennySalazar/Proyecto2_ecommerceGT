package com.ecommerce.gt.ecommerce_gt.auth.dto;


import lombok.Data;

/** Petición de registro de un usuario COMÚN. */
@Data
public class RegisterRequest {
  private String nombre;
  private String correo;
  private String telefono;
  private String password;
}