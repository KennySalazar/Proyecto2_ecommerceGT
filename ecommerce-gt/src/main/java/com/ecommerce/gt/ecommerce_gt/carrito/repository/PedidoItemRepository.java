package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItemPK;

/**
 * REPOSITORIO JPA PARA ITEMS DE PEDIDO.
 * PROPORCIONA MÉTODOS PARA CONSULTAR ITEMS, VERIFICAR COMPRAS
 * Y CALCULAR TOTALES POR VENDEDOR Y RANGOS DE FECHA.
 */
public interface PedidoItemRepository extends JpaRepository<PedidoItem, PedidoItemPK> {

  /**
   * OBTIENE TODOS LOS ITEMS DE UN PEDIDO.
   *
   * @param pedidoId ID DEL PEDIDO.
   * @return LISTA DE ITEMS ASOCIADOS.
   */
  List<PedidoItem> findByPedidoId(Integer pedidoId);

  /**
   * VERIFICA SI UN COMPRADOR YA ADQUIRIÓ UN PRODUCTO ESPECÍFICO.
   *
   * @param productoId  ID DEL PRODUCTO.
   * @param compradorId ID DEL COMPRADOR.
   * @return TRUE SI EXISTE AL MENOS UN ITEM
   */
  boolean existsByProducto_IdAndPedido_CompradorId(Integer productoId, Integer compradorId);

  /**
   * LISTA ITEMS DE PEDIDO DE UN VENDEDOR, ORDENADOS POR FECHA DE CREACIÓN DEL
   * PEDIDO (DESC).
   * INCLUYE FETCH JOIN PARA EVITAR N+1 EN PEDIDO Y PRODUCTO.
   *
   * @param vendedorId ID DEL VENDEDOR.
   * @param pageable   OBJETO PARA LIMITAR CANTIDAD
   * @return LISTA DE ITEMS DEL VENDEDOR.
   */
  @Query("""
        select pi from PedidoItem pi
          join fetch pi.pedido p
          join fetch pi.producto pr
        where pi.vendedorId = :vendedorId
        order by p.fechaCreacion desc
      """)
  List<PedidoItem> findAllByVendedor(@Param("vendedorId") Integer vendedorId, Pageable pageable);

  /**
   * CUENTA EL TOTAL DE ITEMS ASOCIADOS A UN VENDEDOR.
   *
   * @param vendedorId ID DEL VENDEDOR.
   * @return CANTIDAD DE ITEMS.
   */
  @Query("""
        select count(pi) from PedidoItem pi
          where pi.vendedorId = :vendedorId
      """)
  long countByVendedor(@Param("vendedorId") Integer vendedorId);

  /**
   * SUMA EL NETO DEL VENDEDOR EN TODOS SUS ITEMS.
   *
   * @param vendedorId ID DEL VENDEDOR.
   * @return SUMA DEL NETO (LONG).
   */
  @Query("""
        select coalesce(sum(pi.netoVendedor),0)
          from PedidoItem pi join pi.pedido p
        where pi.vendedorId=:vendedorId
      """)
  long sumNetoTotal(@Param("vendedorId") Integer vendedorId);

  /**
   * SUMA EL NETO DEL VENDEDOR EN UN RANGO DE FECHAS
   *
   * @param vendedorId ID DEL VENDEDOR.
   * @param ini        FECHA/HORA INICIAL.
   * @param fin        FECHA/HORA FINAL.
   * @return SUMA DEL NETO EN EL RANGO.
   */
  @Query("""
        select coalesce(sum(pi.netoVendedor),0)
          from PedidoItem pi join pi.pedido p
        where pi.vendedorId=:vendedorId
          and p.fechaCreacion between :ini and :fin
      """)
  long sumNetoRango(@Param("vendedorId") Integer vendedorId,
      @Param("ini") Instant ini,
      @Param("fin") Instant fin);

  /**
   * CUENTA LOS ITEMS DEL VENDEDOR EN UN RANGO DE FECHAS.
   *
   * @param vendedorId ID DEL VENDEDOR.
   * @param ini        FECHA/HORA INICIAL.
   * @param fin        FECHA/HORA FINAL.
   * @return CANTIDAD DE ITEMS EN EL RANGO.
   */
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
