package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.TopClienteCantidadDTO;
import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.VendedorNetoDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * REPOSITORIO DE REPORTES PARA PEDIDOS.
 * PROPORCIONA CONSULTAS AGRUPADAS PARA TOPS DE VENDEDORES Y CLIENTES.
 */
@Repository
public interface PedidoReportesRepository
    extends JpaRepository<com.ecommerce.gt.ecommerce_gt.carrito.entity.Pedido, Integer> {

  /**
   * OBTIENE EL TOP DE VENDEDORES POR MONTO NETO.
   * SUMA EL CAMPO NETO_DEL_VENDEDOR DE LOS ITEMS EN UN RANGO DE FECHAS.
   * RESULTADO ORDENADO DE MAYOR A MENOR MONTO.
   *
   * @param desde    FECHA INICIAL
   * @param hasta    FECHA FINAL
   * @param pageable LIMITADOR DE RESULTADOS
   * @return LISTA DE VendedorNetoDTO CON ID, NOMBRE, NETO Y CANTIDAD DE ITEMS.
   */
  @Query("""
        select new com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.VendedorNetoDTO(
          it.vendedorId,
          coalesce(u.nombre, concat('Usuario #', it.vendedorId)),
          sum(cast(it.netoVendedor as long)),
          count(it)
        )
        from PedidoItem it
        join it.pedido p
        left join com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario u on u.id = it.vendedorId
        where p.fechaCreacion >= :desde and p.fechaCreacion < :hasta
        group by it.vendedorId, u.nombre
        order by sum(cast(it.netoVendedor as long)) desc
      """)
  List<VendedorNetoDTO> topVendedoresNeto(Instant desde, Instant hasta, Pageable pageable);

  /**
   * OBTIENE EL TOP DE CLIENTES POR CANTIDAD DE PEDIDOS.
   * CUENTA PEDIDOS POR CLIENTE EN UN RANGO DE FECHAS.
   * RESULTADO ORDENADO DE MAYOR A MENOR CANTIDAD.
   *
   * @param desde    FECHA INICIAL
   * @param hasta    FECHA FINAL
   * @param pageable LIMITADOR DE RESULTADOS
   * @return LISTA DE TopClienteCantidadDTO CON ID, NOMBRE Y CANTIDAD DE PEDIDOS.
   */
  @Query("""
        select new com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.TopClienteCantidadDTO(
          p.compradorId,
          coalesce(u.nombre, concat('Usuario #', p.compradorId)),
          count(p)
        )
        from Pedido p
        left join com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario u on u.id = p.compradorId
        where p.fechaCreacion >= :desde and p.fechaCreacion < :hasta
        group by p.compradorId, u.nombre
        order by count(p) desc
      """)
  List<TopClienteCantidadDTO> topClientesPorPedidos(@Param("desde") Instant desde,
      @Param("hasta") Instant hasta,
      Pageable pageable);
}
