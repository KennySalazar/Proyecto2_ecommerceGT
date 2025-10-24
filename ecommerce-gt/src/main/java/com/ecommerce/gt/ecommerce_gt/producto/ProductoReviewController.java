package com.ecommerce.gt.ecommerce_gt.producto;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.gt.ecommerce_gt.producto.entity.ProductoReview;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoItemRepository;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos/{productoId}/reviews")
@RequiredArgsConstructor
public class ProductoReviewController {
    private final ProductoReviewRepository reviewRepo;
    private final PedidoItemRepository pedidoItemRepo;
    private final ProductoRepository productoRepo;
    private final JwtUtil jwt;

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

    @PostMapping
    public ResponseEntity<?> crear(@PathVariable Integer productoId,
            @RequestHeader("Authorization") String auth,
            @RequestBody Map<String, Object> body) {
        Integer uid = jwt.getUserIdFromHeader(auth);

        // debe haber comprado
        if (!pedidoItemRepo.existsByProducto_IdAndPedido_CompradorId(productoId, uid))
            return ResponseEntity.status(403).body(Map.of("message", "Debes comprar el producto para reseñarlo"));

        // una sola reseña por usuario
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