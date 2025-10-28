package com.ecommerce.gt.ecommerce_gt.producto;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ecommerce.gt.ecommerce_gt.producto.entity.ProductoReview;

/**
 * REPOSITORIO JPA PARA LAS RESEÑAS (REVIEWS) DE PRODUCTOS.
 * PERMITE CONSULTAR, CONTAR Y CALCULAR PROMEDIOS DE CALIFICACIONES.
 */
@Repository
public interface ProductoReviewRepository extends JpaRepository<ProductoReview, Integer> {

  /**
   * OBTIENE TODAS LAS RESEÑAS DE UN PRODUCTO, ORDENADAS DE MÁS RECIENTE A MÁS
   * ANTIGUA.
   *
   * @param productoId ID DEL PRODUCTO
   * @return LISTA DE RESEÑAS
   */
  List<ProductoReview> findByProductoIdOrderByCreadoEnDesc(Integer productoId);

  /**
   * VERIFICA SI UN USUARIO YA HA DEJADO UNA RESEÑA EN UN PRODUCTO.
   *
   * @param productoId ID DEL PRODUCTO
   * @param usuarioId  ID DEL USUARIO
   * @return TRUE SI YA EXISTE UNA RESEÑA DE ESE USUARIO, FALSE EN CASO CONTRARIO
   */
  boolean existsByProductoIdAndUsuarioId(Integer productoId, Integer usuarioId);

  /**
   * CALCULA EL PROMEDIO DE CALIFICACIONES (RATING) DE UN PRODUCTO.
   * SI NO HAY RESEÑAS, DEVUELVE 0.
   *
   * @param productoId ID DEL PRODUCTO
   * @return PROMEDIO DE RATING
   */
  @Query("select coalesce(avg(r.rating),0) from ProductoReview r where r.producto.id=:pid")
  Double avgByProducto(@Param("pid") Integer productoId);

  /**
   * CUENTA CUÁNTAS RESEÑAS TIENE UN PRODUCTO.
   *
   * @param productoId ID DEL PRODUCTO
   * @return NÚMERO DE RESEÑAS
   */
  int countByProductoId(Integer productoId);
}
