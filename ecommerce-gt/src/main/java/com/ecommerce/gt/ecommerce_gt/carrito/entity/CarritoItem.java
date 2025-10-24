package com.ecommerce.gt.ecommerce_gt.carrito.entity;

import java.time.Instant;

import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "carrito_items")
@Getter
@Setter
public class CarritoItem {

    @EmbeddedId
    private CarritoItemPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("carritoId")
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Integer precio;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn = Instant.now();
}