package com.ecommerce.gt.ecommerce_gt.producto.dto;

import lombok.Data;

@Data
public class ProductoActualizarRequest {
    private String nombre;
    private String descripcion;
    private String estadoArticulo;
    private Integer categoriaId;
    private Long precioCents;
    private Integer stock;
}
