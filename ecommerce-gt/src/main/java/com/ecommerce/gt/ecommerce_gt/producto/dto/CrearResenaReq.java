package com.ecommerce.gt.ecommerce_gt.producto.dto;

import lombok.Data;

@Data
public class CrearResenaReq {
    private short rating;
    private String comentario;
}