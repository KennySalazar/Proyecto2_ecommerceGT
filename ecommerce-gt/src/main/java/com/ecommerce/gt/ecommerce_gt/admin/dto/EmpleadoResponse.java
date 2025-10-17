package com.ecommerce.gt.ecommerce_gt.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmpleadoResponse {
    private Integer id;
    private String nombre;
    private String correo;
    private String telefono;
    private String rol;
    private Boolean activo;
}