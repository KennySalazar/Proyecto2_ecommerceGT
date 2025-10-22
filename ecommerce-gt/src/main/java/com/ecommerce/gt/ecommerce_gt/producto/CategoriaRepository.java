package com.ecommerce.gt.ecommerce_gt.producto;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.gt.ecommerce_gt.producto.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}