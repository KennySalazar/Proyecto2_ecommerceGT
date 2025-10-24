package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.producto.dto.*;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService service;
    private final JwtUtil jwt;

    @GetMapping("/publico")
    public ResponseEntity<Page<ProductoResponse>> publico(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {
        return ResponseEntity.ok(service.listarPublico(pagina, tamanio));
    }

    @GetMapping("/mis")
    public ResponseEntity<Page<ProductoResponse>> mis(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        Integer userId = jwt.getUserIdFromHeader(auth);
        return ResponseEntity.ok(service.listarMisProductos(userId, pagina, tamanio));
    }

    @PostMapping(value = "/mis", consumes = { "multipart/form-data" })
    public ResponseEntity<ProductoResponse> crear(
            @RequestHeader("Authorization") String auth,
            @RequestPart("dto") ProductoCrearRequest dto,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        Integer userId = jwt.getUserIdFromHeader(auth);
        return ResponseEntity.ok(service.crear(userId, dto, imagen));
    }

    @PutMapping(value = "/mis/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ProductoResponse> actualizar(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer id,
            @RequestPart("dto") ProductoActualizarRequest dto,
            @RequestPart(value = "imagen", required = false) MultipartFile nuevaImagen) {
        Integer userId = jwt.getUserIdFromHeader(auth);
        return ResponseEntity.ok(service.actualizar(id, userId, dto, nuevaImagen));
    }

    @GetMapping("/mis/{id}")
    public ResponseEntity<ProductoResponse> obtenerMio(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer id) {
        Integer userId = jwt.getUserIdFromHeader(auth);
        return ResponseEntity.ok(service.obtenerMio(userId, id));
    }
}
