package com.ecommerce.gt.ecommerce_gt.carrito.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class CarritoItemPK implements Serializable {
    private Integer carritoId;
    private Integer productoId;
}