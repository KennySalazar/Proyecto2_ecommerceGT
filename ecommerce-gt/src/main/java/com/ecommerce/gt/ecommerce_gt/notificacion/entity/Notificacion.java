package com.ecommerce.gt.ecommerce_gt.notificacion.entity;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(nullable = false, length = 160)
    private String asunto;

    @Column(nullable = false, columnDefinition = "text")
    private String cuerpo;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(nullable = false)
    private boolean enviado = false;

    private Instant enviadoEn;
    @Column(nullable = false)
    private Instant creadoEn = Instant.now();
}