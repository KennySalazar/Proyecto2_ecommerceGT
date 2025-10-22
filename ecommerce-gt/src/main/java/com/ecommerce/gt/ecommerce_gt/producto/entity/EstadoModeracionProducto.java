package com.ecommerce.gt.ecommerce_gt.producto.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_moderacion_producto", schema = "ecommerce")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoModeracionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 'PENDIENTE','APROBADO','RECHAZADO'
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(name = "descripcion", nullable = false, length = 120)
    private String descripcion;
}
