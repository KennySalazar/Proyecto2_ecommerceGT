package com.ecommerce.gt.ecommerce_gt.producto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Long precioCents;
    private Integer stock;
    private String estadoArticulo;
    private String categoria;
    private String estadoModeracion;
    private String imageUrl;
    private Integer vendedorId;
    private Double ratingPromedio;
    private Integer totalResenas;
    private Integer categoriaId;
}
