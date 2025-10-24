package com.ecommerce.gt.ecommerce_gt.producto.entity;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "producto_reviews")
@Getter
@Setter
public class ProductoReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private Short rating;
    @Column(nullable = false)
    private String comentario;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn = Instant.now();
}