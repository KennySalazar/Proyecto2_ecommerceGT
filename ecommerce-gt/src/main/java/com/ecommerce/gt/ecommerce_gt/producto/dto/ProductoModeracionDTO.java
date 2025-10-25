package com.ecommerce.gt.ecommerce_gt.producto.dto;

public record ProductoModeracionDTO(
        Integer id,
        String nombre,
        String categoria,
        Integer precioCents,
        Integer stock,
        String vendedorNombre,
        String imagenUrl,
        String creadoEnIso) {
}
