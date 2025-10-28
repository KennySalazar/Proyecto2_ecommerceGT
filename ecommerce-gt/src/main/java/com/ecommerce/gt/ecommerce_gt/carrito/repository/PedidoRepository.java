package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.Pedido;

/**
 * REPOSITORIO JPA PARA LA ENTIDAD PEDIDO.
 * PERMITE CONSULTAR PEDIDOS POR COMPRADOR, ESTADO Y FECHAS.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

  /**
   * OBTIENE TODOS LOS PEDIDOS DE UN COMPRADOR, ORDENADOS POR FECHA DE CREACIÓN
   * (DESCENDENTE).
   *
   * @param compradorId ID DEL COMPRADOR.
   * @return LISTA DE PEDIDOS DEL USUARIO.
   */
  List<Pedido> findByCompradorIdOrderByFechaCreacionDesc(Integer compradorId);

  /**
   * OBTIENE UNA PÁGINA DE PEDIDOS FILTRADOS POR EL CÓDIGO DE SU ESTADO.
   *
   * @param codigo   CÓDIGO DEL ESTADO
   * @param pageable OBJETO DE PAGINACIÓN.
   * @return PÁGINA DE PEDIDOS CON EL ESTADO INDICADO.
   */
  @Query("""
        select p from Pedido p
        where p.estadoPedido.codigo = :codigo
        order by p.fechaCreacion desc
      """)
  Page<Pedido> findByEstadoCodigo(@Param("codigo") String codigo, Pageable pageable);

  /**
   * CALCULA LA SUMA TOTAL DE LAS COMISIONES GENERADAS EN UN RANGO DE FECHAS.
   *
   * @param desde FECHA DE INICIO
   * @param hasta FECHA FINAL
   * @return SUMA DE COMISIONES
   */
  @Query("""
        select coalesce(sum(p.totalComision), 0)
        from Pedido p
        where p.fechaCreacion >= :desde
          and p.fechaCreacion < :hasta
      """)
  Long sumaComisionPorRango(Instant desde, Instant hasta);
}
