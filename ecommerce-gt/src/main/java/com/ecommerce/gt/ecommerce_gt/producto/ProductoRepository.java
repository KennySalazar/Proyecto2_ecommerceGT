package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.producto.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Page<Producto> findByVendedorId(Integer vendedorId, Pageable pageable);

    Page<Producto> findByEstadoModCodigo(String codigo, Pageable pageable); // PÚBLICO: APROBADO
}