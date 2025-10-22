package com.ecommerce.gt.ecommerce_gt.producto;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.gt.ecommerce_gt.producto.entity.EstadoModeracionProducto;

import java.util.Optional;

public interface EstadoModeracionProductoRepository extends JpaRepository<EstadoModeracionProducto, Integer> {
    Optional<EstadoModeracionProducto> findByCodigo(String codigo); // 'PENDIENTE','APROBADO','RECHAZADO'
}
