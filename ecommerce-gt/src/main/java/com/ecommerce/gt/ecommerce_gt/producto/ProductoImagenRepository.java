package com.ecommerce.gt.ecommerce_gt.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.gt.ecommerce_gt.producto.entity.ProductoImagen;

import java.util.Optional;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Integer> {

    Optional<ProductoImagen> findFirstByProductoIdOrderByIdDesc(Integer productoId);
}
