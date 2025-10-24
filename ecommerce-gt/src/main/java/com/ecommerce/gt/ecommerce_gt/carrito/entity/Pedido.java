package com.ecommerce.gt.ecommerce_gt.carrito.entity;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comprador_id", nullable = false)
    private Integer compradorId;

    @Column(name = "total_bruto", nullable = false)
    private Integer totalBruto;

    @Column(name = "total_comision", nullable = false)
    private Integer totalComision;

    @Column(name = "total_neto_vendedores", nullable = false)
    private Integer totalNetoVendedores;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_pedido_id", nullable = false)
    private EstadoPedido estadoPedido;

    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion = Instant.now();

    @Column(name = "fecha_estimada_entrega")
    private LocalDate fechaEstimadaEntrega;

    @Column(name = "fecha_entregado")
    private LocalDate fechaEntregado;
}