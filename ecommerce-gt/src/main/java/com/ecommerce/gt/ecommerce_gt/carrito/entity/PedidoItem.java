package com.ecommerce.gt.ecommerce_gt.carrito.entity;

import java.time.LocalDate;

import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pedido_items")
@Getter
@Setter
public class PedidoItem {

    @EmbeddedId
    private PedidoItemPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pedidoId")
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "vendedor_id", nullable = false)
    private Integer vendedorId;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Integer precioUnitario;

    @Column(nullable = false)
    private Integer subtotal;

    @Column(nullable = false)
    private Integer comision;

    @Column(name = "neto_vendedor", nullable = false)
    private Integer netoVendedor;

    @Column(name = "debe_entregarse_el")
    private LocalDate debeEntregarseEl;

    @Column(name = "entregado_el")
    private LocalDate entregadoEl;
}