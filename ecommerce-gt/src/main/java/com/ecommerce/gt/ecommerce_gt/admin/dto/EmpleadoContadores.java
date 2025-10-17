package com.ecommerce.gt.ecommerce_gt.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmpleadoContadores {
    private long total;
    private long moderadores;
    private long logistica;
    private long administradores;
    private Long comunes;
}
