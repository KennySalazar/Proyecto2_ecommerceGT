package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.ProductoTopDTO;
import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.TopClienteCantidadDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * REPOSITORIO DE CONSULTAS PERSONALIZADAS PARA REPORTES DE PEDIDOS.
 * PERMITE OBTENER INFORMACIÓN ESTADÍSTICA SOBRE PRODUCTOS Y VENDEDORES.
 */
@Repository
public interface PedidoItemReportesRepository
                extends JpaRepository<com.ecommerce.gt.ecommerce_gt.carrito.entity.PedidoItem, Object> {

        /**
         * OBTIENE EL TOP DE PRODUCTOS MÁS VENDIDOS EN UN RANGO DE FECHAS.
         * SE AGRUPAN LOS ITEMS DE PEDIDO POR PRODUCTO Y SE ORDENA POR CANTIDAD TOTAL.
         *
         * @param desde    FECHA INICIAL DEL RANGO
         * @param hasta    FECHA FINAL DEL RANGO
         * @param pageable OBJETO PARA LIMITAR LA CANTIDAD DE RESULTADOS
         * @return LISTA DE ProductoTopDTO CON DATOS DE PRODUCTO, UNIDADES Y TOTAL
         *         VENDIDO.
         */
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

        /**
         * OBTIENE EL TOP DE VENDEDORES QUE MÁS UNIDADES HAN VENDIDO.
         * SE AGRUPAN LOS ITEMS POR ID DE VENDEDOR Y SE ORDENA POR CANTIDAD TOTAL.
         * SI UN VENDEDOR NO TIENE NOMBRE REGISTRADO, SE MUESTRA COMO "Usuario #ID".
         *
         * @param desde    FECHA INICIAL DEL RANGO
         * @param hasta    FECHA FINAL DEL RANGO
         * @param pageable OBJETO PARA LIMITAR LA CANTIDAD DE RESULTADOS
         * @return LISTA DE TopClienteCantidadDTO CON ID, NOMBRE Y UNIDADES VENDIDAS.
         */
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
