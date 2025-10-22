package com.ecommerce.gt.ecommerce_gt.producto.entity;

import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;

    @Column(nullable = false, length = 140)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_articulo", nullable = false, length = 10)
    private EstadoArticulo estadoArticulo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estado_mod_id")
    private EstadoModeracionProducto estadoMod;

    @Column(name = "ultima_revision_en")
    private Instant ultimaRevisionEn;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;
}
