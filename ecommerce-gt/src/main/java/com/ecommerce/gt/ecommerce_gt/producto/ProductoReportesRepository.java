package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.admin.reportes.dto.TopClienteCantidadDTO;
import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoReportesRepository extends JpaRepository<Producto, Integer> {

    // Top 10 clientes que más productos tienen a la venta
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