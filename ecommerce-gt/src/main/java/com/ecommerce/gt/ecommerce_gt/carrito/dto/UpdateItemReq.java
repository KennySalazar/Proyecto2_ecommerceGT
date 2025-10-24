package com.ecommerce.gt.ecommerce_gt.carrito.dto;

import lombok.Data;

@Data
public class UpdateItemReq {
    private Integer productoId;
    private Integer cantidad;
}