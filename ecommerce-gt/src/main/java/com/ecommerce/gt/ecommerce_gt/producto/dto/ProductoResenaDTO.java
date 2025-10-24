package com.ecommerce.gt.ecommerce_gt.producto.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductoResenaDTO {
    private Integer id;
    private Integer usuarioId;
    private short rating;
    private String comentario;
    private Instant creadoEn;
}