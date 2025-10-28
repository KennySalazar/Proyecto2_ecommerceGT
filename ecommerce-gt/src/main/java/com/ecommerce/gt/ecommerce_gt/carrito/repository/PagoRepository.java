package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.Pago;

/**
 * REPOSITORIO JPA PARA LA ENTIDAD PAGO.
 * PERMITE REALIZAR OPERACIONES CRUD SOBRE LOS PAGOS
 * RELACIONADOS A LOS PEDIDOS DEL SISTEMA.
 */
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}
