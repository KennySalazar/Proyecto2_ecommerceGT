package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.ProductoTopDTO;
import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.TopClienteCantidadDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PedidoItemReportesRepository
        extends JpaRepository<com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem, Object> {

    // Top 10 productos más vendidos
    @Query("""
              select new com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.ProductoTopDTO(
                 it.producto.id,
                 it.producto.nombre,
                 sum(it.cantidad),
                 sum(it.subtotal)
              )
              from PedidoItem it
              join it.pedido p
              where p.fechaCreacion >= :desde and p.fechaCreacion < :hasta
              group by it.producto.id, it.producto.nombre
              order by sum(it.cantidad) desc
            """)
    List<ProductoTopDTO> topProductos(@Param("desde") Instant desde,
            @Param("hasta") Instant hasta,
            Pageable pageable);

    // Top 5 clientes que más productos han vendido
    @Query("""
              select new com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.TopClienteCantidadDTO(
                it.vendedorId,
                coalesce(u.nombre, concat('Usuario #', it.vendedorId)),
                sum(it.cantidad)
              )
              from PedidoItem it
              join it.pedido p
              left join com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario u on u.id = it.vendedorId
              where p.fechaCreacion >= :desde and p.fechaCreacion < :hasta
              group by it.vendedorId, u.nombre
              order by sum(it.cantidad) desc
            """)
    List<TopClienteCantidadDTO> topVendedoresPorUnidades(@Param("desde") Instant desde,
            @Param("hasta") Instant hasta,
            Pageable pageable);
}