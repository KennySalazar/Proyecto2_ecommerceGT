package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByCompradorIdOrderByFechaCreacionDesc(Integer compradorId);
}