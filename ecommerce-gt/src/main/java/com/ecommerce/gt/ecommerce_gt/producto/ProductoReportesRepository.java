package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.TopClienteCantidadDTO;
import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * REPOSITORIO PARA REPORTES DE PRODUCTOS.
 * PERMITE GENERAR ESTADÍSTICAS COMO LOS VENDEDORES CON MÁS PRODUCTOS A LA
 * VENTA.
 */
public interface ProductoReportesRepository extends JpaRepository<Producto, Integer> {

    /**
     * OBTIENE EL TOP DE VENDEDORES CON MAYOR INVENTARIO PUBLICADO.
     * CONSIDERA SOLO PRODUCTOS CON STOCK DISPONIBLE.
     *
     * @param pageable OBJETO PARA LIMITAR Y PAGINAR LOS RESULTADOS
     * @return LISTA DE TopClienteCantidadDTO CON ID, NOMBRE Y CANTIDAD DE
     *         PRODUCTOS.
     */
    @Query("""
            select new com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.TopClienteCantidadDTO(
                pr.vendedor.id,
                coalesce(u.nombre, concat('Usuario #', pr.vendedor.id)),
                count(pr)
            )
            from Producto pr
            left join com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario u on u.id = pr.vendedor.id
            where (pr.stock is null or pr.stock > 0)
            group by pr.vendedor.id, u.nombre
            order by count(pr) desc
            """)
    List<TopClienteCantidadDTO> topInventarioVendedores(Pageable pageable);
}
