package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import java.time.Instant;

public record GananciaItemDTO(
        Integer pedidoId,
        Instant fecha,
        Integer productoId,
        String productoNombre,
        Integer cantidad,
        Integer precioUnitario,
        Integer subtotal,
        Integer comision,
        Integer netoVendedor,
        String estadoPedido,
        String imagenUrl) {
}