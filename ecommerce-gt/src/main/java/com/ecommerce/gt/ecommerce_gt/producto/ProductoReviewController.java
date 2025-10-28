package com.ecommerce.gt.ecommerce_gt.producto;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.gt.ecommerce_gt.producto.entity.ProductoReview;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoItemRepository;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import lombok.RequiredArgsConstructor;

/**
 * CONTROLADOR DE RESEÑAS (REVIEWS) DE PRODUCTOS.
 * PERMITE LISTAR RESEÑAS Y CREAR UNA NUEVA SI EL USUARIO COMPRÓ EL PRODUCTO.
 *
 * RUTA BASE: /api/productos/{productoId}/reviews
 */
@RestController
@RequestMapping("/api/productos/{productoId}/reviews")
@RequiredArgsConstructor
public class ProductoReviewController {

    /** REPOSITORIO DE RESEÑAS DE PRODUCTO */
    private final ProductoReviewRepository reviewRepo;
    /** REPOSITORIO DE ITEMS DE PEDIDO (PARA VALIDAR COMPRA) */
    private final PedidoItemRepository pedidoItemRepo;
    /** REPOSITORIO DE PRODUCTOS */
    private final ProductoRepository productoRepo;
    /** UTILIDAD JWT PARA OBTENER ID DE USUARIO DESDE EL TOKEN */
    private final JwtUtil jwt;

    /**
     * LISTA RESEÑAS DE UN PRODUCTO Y DEVUELVE DATOS EXTRAS:
     * - PROMEDIO DE RATING
     * - TOTAL DE RESEÑAS
     * - SI EL USUARIO PUEDE COMENTAR (COMPRÓ Y NO HA RESEÑADO)
     *
     * @param productoId ID DEL PRODUCTO
     * @param auth       TOKEN JWT (OPCIONAL) PARA DETERMINAR SI PUEDE COMENTAR
     * @return MAPA CON PROMEDIO, TOTAL, PUEDECOMENTAR E ITEMS
     */
    @GetMapping
    public Map<String, Object> listar(@PathVariable Integer productoId,
            @RequestHeader(value = "Authorization", required = false) String auth) {
        var data = reviewRepo.findByProductoIdOrderByCreadoEnDesc(productoId).stream().map(r -> Map.of(
                "usuario", "Usuario #" + r.getUsuarioId(),
                "rating", r.getRating(),
                "comentario", r.getComentario())).toList();

        Double avg = reviewRepo.avgByProducto(productoId);
        int total = reviewRepo.countByProductoId(productoId);

        boolean puedeComentar = false;
        if (auth != null && !auth.isBlank()) {
            Integer uid = jwt.getUserIdFromHeader(auth);
            puedeComentar = pedidoItemRepo.existsByProducto_IdAndPedido_CompradorId(productoId, uid)
                    && !reviewRepo.existsByProductoIdAndUsuarioId(productoId, uid);
        }

        return Map.of(
                "promedio", avg == null ? 0.0 : avg,
                "total", total,
                "puedeComentar", puedeComentar,
                "items", data);
    }

    /**
     * CREA UNA RESEÑA PARA UN PRODUCTO.
     * SOLO PERMITIDO SI EL USUARIO COMPRÓ EL PRODUCTO Y AÚN NO HA RESEÑADO.
     *
     * @param productoId ID DEL PRODUCTO
     * @param auth       TOKEN JWT DEL USUARIO
     * @param body       CUERPO CON CAMPOS "rating" (1-5) Y "comentario"
     * @return 200 OK SI SE GUARDA, MENSAJE DE ERROR EN CASO CONTRARIO
     */
    @PostMapping
    public ResponseEntity<?> crear(@PathVariable Integer productoId,
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> body) {
        Integer uid = jwt.getUserIdFromHeader(auth);

        // DEBE HABER COMPRADO EL PRODUCTO
        if (!pedidoItemRepo.existsByProducto_IdAndPedido_CompradorId(productoId, uid))
            return ResponseEntity.status(403).body(Map.of("message", "Debes comprar el producto para reseñarlo"));

        // SOLO UNA RESEÑA POR USUARIO Y PRODUCTO
        if (reviewRepo.existsByProductoIdAndUsuarioId(productoId, uid))
            return ResponseEntity.badRequest().body(Map.of("message", "Ya enviaste una reseña"));

        var p = productoRepo.findById(productoId).orElseThrow();
        var r = new ProductoReview();
        r.setProducto(p);
        r.setUsuarioId(uid);
        r.setRating(Short.valueOf(String.valueOf(body.get("rating"))));
        r.setComentario(String.valueOf(body.get("comentario")));
        reviewRepo.save(r);

        return ResponseEntity.ok(Map.of("ok", true));
    }
}
