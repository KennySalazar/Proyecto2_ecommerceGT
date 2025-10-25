package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import java.time.Instant;
import java.time.LocalDate;

public record PedidoLogisticaDTO(
        Integer id,
        String compradorNombre,
        Instant fechaCreacion,
        LocalDate fechaEstimadaEntrega,
        Integer totalBruto,
        String estadoCodigo) {
}