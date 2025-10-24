package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findFirstByUsuarioIdAndEstaVigenteTrueOrderByIdDesc(Integer usuarioId);
}