package com.ecommerce.gt.ecommerce_gt.usuario.repository;

import com.ecommerce.gt.ecommerce_gt.usuario.entity.Rol;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByCodigo(String codigo);
}