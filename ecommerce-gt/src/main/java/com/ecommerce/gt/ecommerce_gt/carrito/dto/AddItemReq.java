package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddItemReq {
    private Integer productoId;
    private Integer cantidad;
}
