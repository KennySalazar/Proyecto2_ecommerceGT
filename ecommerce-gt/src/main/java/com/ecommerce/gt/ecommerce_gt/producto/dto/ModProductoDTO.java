package com.ecommerce.gt.ecommerce_gt.producto.dto;

public record ModProductoDTO(
        Integer id,
        String nombre,
        String categoria,
        Long precioCents,
        Integer stock,
        String vendedorNombre,
        String imagenUrl,
        String creadoEnIso,
        String estadoMod,
        String comentarioRechazo) {
}