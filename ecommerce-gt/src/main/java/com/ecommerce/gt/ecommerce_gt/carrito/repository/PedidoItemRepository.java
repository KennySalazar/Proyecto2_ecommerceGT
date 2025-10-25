package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItemPK;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, PedidoItemPK> {
  List<PedidoItem> findByPedidoId(Integer pedidoId);

  boolean existsByProducto_IdAndPedido_CompradorId(Integer productoId, Integer compradorId);

  // Lista de ventas del vendedor (paginada)
  @Query("""
        select pi from PedidoItem pi
          join fetch pi.pedido p
          join fetch pi.producto pr
        where pi.vendedorId = :vendedorId
        order by p.fechaCreacion desc
      """)
  List<PedidoItem> findAllByVendedor(@Param("vendedorId") Integer vendedorId, Pageable pageable);

  @Query("""
        select count(pi) from PedidoItem pi
          where pi.vendedorId = :vendedorId
      """)
  long countByVendedor(@Param("vendedorId") Integer vendedorId);

  // Sumas
  @Query("""
        select coalesce(sum(pi.netoVendedor),0)
          from PedidoItem pi join pi.pedido p
        where pi.vendedorId=:vendedorId
      """)
  long sumNetoTotal(@Param("vendedorId") Integer vendedorId);

  @Query("""
        select coalesce(sum(pi.netoVendedor),0)
          from PedidoItem pi join pi.pedido p
        where pi.vendedorId=:vendedorId
          and p.fechaCreacion between :ini and :fin
      """)
  long sumNetoRango(@Param("vendedorId") Integer vendedorId,
      @Param("ini") Instant ini,
      @Param("fin") Instant fin);

  @Query("""
        select count(pi)
          from PedidoItem pi join pi.pedido p
        where pi.vendedorId=:vendedorId
          and p.fechaCreacion between :ini and :fin
      """)
  int countRango(@Param("vendedorId") Integer vendedorId,
      @Param("ini") Instant ini,
      @Param("fin") Instant fin);
}