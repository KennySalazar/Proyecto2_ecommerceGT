package com.ecommerce.gt.ecommerce_gt.usuario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "usuarios", schema = "ecommerce")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    @Column(unique = true)
    private String correo;

    private String telefono;

    @Column(name = "hash_password")
    private String hashPassword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @Column(name = "esta_activo")
    private Boolean estaActivo;

    public Usuario(Integer id) {
        this.id = id;
    }
}