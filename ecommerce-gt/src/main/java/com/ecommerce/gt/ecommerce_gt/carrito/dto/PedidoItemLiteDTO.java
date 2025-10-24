package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PedidoItemLiteDTO {
    private Integer productoId;
    private String nombre;
    private String imagenUrl;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer subtotal;
}