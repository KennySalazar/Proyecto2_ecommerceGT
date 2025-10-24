package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.CarritoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.CarritoItemPK;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, CarritoItemPK> {

    List<CarritoItem> findByCarritoId(Integer carritoId);

    Optional<CarritoItem> findByCarritoIdAndProductoId(Integer carritoId, Integer productoId);
}