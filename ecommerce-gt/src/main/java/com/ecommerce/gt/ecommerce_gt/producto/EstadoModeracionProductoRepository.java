package com.ecommerce.gt.ecommerce_gt.producto;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.gt.ecommerce_gt.producto.entity.EstadoModeracionProducto;

import java.util.Optional;

/**
 * REPOSITORIO JPA PARA LOS ESTADOS DE MODERACIÓN DE PRODUCTOS.
 * PERMITE CONSULTAR, GUARDAR Y ACTUALIZAR ESTADOS COMO "PENDIENTE", "APROBADO"
 * O "RECHAZADO".
 */
public interface EstadoModeracionProductoRepository extends JpaRepository<EstadoModeracionProducto, Integer> {

    /**
     * BUSCA UN ESTADO DE MODERACIÓN POR SU CÓDIGO.
     *
     * @param codigo CÓDIGO DEL ESTADO
     * @return UN Optional CON EL ESTADO SI EXISTE, O VACÍO SI NO SE ENCUENTRA.
     */
    Optional<EstadoModeracionProducto> findByCodigo(String codigo);
}
