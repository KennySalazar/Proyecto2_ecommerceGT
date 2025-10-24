package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.Pago;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
}