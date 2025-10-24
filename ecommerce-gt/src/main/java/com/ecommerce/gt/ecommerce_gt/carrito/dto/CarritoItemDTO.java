package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarritoItemDTO {
        private Integer productoId;
        private String nombre;
        private String imagenUrl;
        private Integer precio;
        private Integer cantidad;
        private Integer subtotal;
        private Integer disponible;

}