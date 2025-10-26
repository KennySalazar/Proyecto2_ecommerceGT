package com.ecommerce.gt.ecommerce_gt.admin.reportes.dto;

public record VendedorNetoDTO(
        Integer usuarioId,
        String nombre,
        Long totalNeto,
        Long totalVentas) {
}