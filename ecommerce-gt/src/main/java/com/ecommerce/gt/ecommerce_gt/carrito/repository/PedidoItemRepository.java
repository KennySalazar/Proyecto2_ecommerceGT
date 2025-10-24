package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItemPK;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, PedidoItemPK> {
  List<PedidoItem> findByPedidoId(Integer pedidoId);

  boolean existsByProducto_IdAndPedido_CompradorId(Integer productoId, Integer compradorId);
}