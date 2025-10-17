package com.ecommerce.gt.ecommerce_gt.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoCrearRequest {
    private String nombre;
    private String correo;
    private String telefono;
    private String contrasena;
    private String rolCodigo; // "MODERADOR" | "LOGISTICA" | "ADMIN"
}
