package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Page<Producto> findByVendedorId(Integer vendedorId, Pageable pageable);

    Page<Producto> findByEstadoModCodigo(String codigo, Pageable pageable);

    @Modifying
    @Query("update Producto p set p.stock = p.stock - :cant where p.id = :id and p.stock >= :cant")
    int reservarStock(@Param("id") Integer id, @Param("cant") int cant);

    @Modifying
    @Query("update Producto p set p.stock = p.stock + :cant where p.id = :id")
    int liberarStock(@Param("id") Integer id, @Param("cant") int cant);

    @Query("select p.stock from Producto p where p.id = :id")
    Integer stockRestante(@Param("id") Integer id);
}