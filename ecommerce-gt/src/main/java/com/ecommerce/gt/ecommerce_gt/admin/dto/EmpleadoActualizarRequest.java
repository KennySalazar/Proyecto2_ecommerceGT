package com.ecommerce.gt.ecommerce_gt.admin.dto;

import lombok.Data;

@Data
public class EmpleadoActualizarRequest {
    private String nombre;
    private String correo;
    private String telefono;
    private String rolCodigo;

}