package com.ecommerce.gt.ecommerce_gt.carrito.dto;

public record GananciaResumenDTO(
        long totalNeto,
        long netoHoy,
        long netoMes,
        int ventasHoy,
        int ventasMes) {
}