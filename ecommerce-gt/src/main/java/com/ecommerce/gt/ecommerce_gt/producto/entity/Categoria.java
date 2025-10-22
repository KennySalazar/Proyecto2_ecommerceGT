package com.ecommerce.gt.ecommerce_gt.producto.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias", schema = "ecommerce")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, unique = true, length = 40)
    private String nombre;
}
