package com.ecommerce.gt.ecommerce_gt.producto;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ecommerce.gt.ecommerce_gt.producto.entity.ProductoReview;

@Repository
public interface ProductoReviewRepository extends JpaRepository<ProductoReview, Integer> {
  List<ProductoReview> findByProductoIdOrderByCreadoEnDesc(Integer productoId);

  boolean existsByProductoIdAndUsuarioId(Integer productoId, Integer usuarioId);

  @Query("select coalesce(avg(r.rating),0) from ProductoReview r where r.producto.id=:pid")
  Double avgByProducto(@Param("pid") Integer productoId);

  int countByProductoId(Integer productoId);
}