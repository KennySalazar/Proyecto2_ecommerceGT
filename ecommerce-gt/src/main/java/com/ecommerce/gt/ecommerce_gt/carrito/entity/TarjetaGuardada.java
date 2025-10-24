package com.ecommerce.gt.ecommerce_gt.carrito.entity;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tarjetas_guardadas")
@Getter
@Setter
public class TarjetaGuardada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "token_pasarela", nullable = false, length = 140)
    private String tokenPasarela;

    @Column(nullable = false, length = 4)
    private String ultimos4;

    @Column(nullable = false, length = 20)
    private String marca;

    @Column(name = "expiracion_mes", nullable = false)
    private Short expiracionMes;

    @Column(name = "expiracion_anio", nullable = false)
    private Short expiracionAnio;

    @Column(nullable = false, length = 120)
    private String titular;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn = Instant.now();
}