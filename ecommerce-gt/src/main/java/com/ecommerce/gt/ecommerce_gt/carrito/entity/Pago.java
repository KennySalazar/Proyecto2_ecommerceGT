package com.ecommerce.gt.ecommerce_gt.carrito.entity;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pagos")
@Getter
@Setter
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private Long monto;

    @Column(nullable = false, length = 20)
    private String metodo;

    @Column(name = "tarjeta_guardada_id")
    private Integer tarjetaGuardadaId;

    @Column(name = "referencia_pasarela")
    private String referenciaPasarela;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn = Instant.now();
}