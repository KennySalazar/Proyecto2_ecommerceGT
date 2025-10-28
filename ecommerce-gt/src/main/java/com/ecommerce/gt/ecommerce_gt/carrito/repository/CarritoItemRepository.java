package com.ecommerce.gt.ecommerce_gt.carrito.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.carrito.entity.CarritoItem;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.CarritoItemPK;

/**
 * REPOSITORIO JPA PARA LOS ITEMS DEL CARRITO.
 * PERMITE CONSULTAR, GUARDAR Y ELIMINAR PRODUCTOS
 * QUE PERTENECEN A UN CARRITO DE COMPRAS.
 */
@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, CarritoItemPK> {

    /**
     * BUSCA TODOS LOS PRODUCTOS (ITEMS) DE UN CARRITO ESPECÍFICO.
     *
     * @param carritoId ID DEL CARRITO.
     * @return LISTA DE ITEMS QUE PERTENECEN A ESE CARRITO.
     */
    List<CarritoItem> findByCarritoId(Integer carritoId);

    /**
     * BUSCA UN PRODUCTO ESPECÍFICO DENTRO DE UN CARRITO.
     *
     * @param carritoId  ID DEL CARRITO.
     * @param productoId ID DEL PRODUCTO A BUSCAR.
     * @return ITEM DEL CARRITO SI EXISTE
     */
    Optional<CarritoItem> findByCarritoIdAndProductoId(Integer carritoId, Integer productoId);
}
