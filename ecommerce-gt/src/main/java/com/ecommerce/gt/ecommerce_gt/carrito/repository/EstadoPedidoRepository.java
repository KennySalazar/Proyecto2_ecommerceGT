package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.EstadoPedido;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Integer> {

    Optional<EstadoPedido> findByCodigo(String codigo);
}