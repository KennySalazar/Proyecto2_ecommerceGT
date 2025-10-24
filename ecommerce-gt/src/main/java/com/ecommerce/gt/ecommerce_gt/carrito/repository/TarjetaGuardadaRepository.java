package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.TarjetaGuardada;

@Repository
public interface TarjetaGuardadaRepository extends JpaRepository<TarjetaGuardada, Integer> {

    List<TarjetaGuardada> findByUsuarioId(Integer usuarioId);
}