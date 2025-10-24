package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PedidoResumenDTO {
    private Integer id;
    private Instant fecha;
    private Integer total;
    private String estado;
    private LocalDate fechaEstimadaEntrega;
    private List<PedidoItemLiteDTO> items;
}