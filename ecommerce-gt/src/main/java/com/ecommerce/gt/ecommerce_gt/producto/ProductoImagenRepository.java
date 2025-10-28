package com.ecommerce.gt.ecommerce_gt.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.producto.entity.ProductoImagen;

import java.util.Optional;

/**
 * REPOSITORIO DE IMÁGENES DE PRODUCTOS.
 * PERMITE CONSULTAR, GUARDAR Y OBTENER LA IMAGEN MÁS RECIENTE DE UN PRODUCTO.
 */
@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Integer> {

    /**
     * BUSCA LA ÚLTIMA IMAGEN ASOCIADA A UN PRODUCTO SEGÚN SU ID.
     * ORDENA POR ID DESCENDENTE Y DEVUELVE LA PRIMERA ENCONTRADA.
     *
     * @param productoId ID DEL PRODUCTO.
     * @return OPTIONAL CON LA IMAGEN MÁS RECIENTE O VACÍO SI NO TIENE.
     */
    Optional<ProductoImagen> findFirstByProductoIdOrderByIdDesc(Integer productoId);
}
