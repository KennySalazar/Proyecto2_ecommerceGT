package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.producto.dto.ProductoModeracionDTO;
import com.ecommerce.gt.ecommerce_gt.producto.dto.ModProductoDTO;
import com.ecommerce.gt.ecommerce_gt.producto.dto.ModeracionReq;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderador")
@RequiredArgsConstructor
public class ModeradorController {

    private final ModeracionService service;
    private final JwtUtil jwt;

    @GetMapping("/solicitudes")
    public Page<ProductoModeracionDTO> pendientes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {
        return service.listarPendientes(pagina, tamanio);
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public ResponseEntity<?> aprobar(@RequestHeader("Authorization") String auth,
            @PathVariable Integer id) {
        var mid = jwt.getUserIdFromHeader(auth);
        service.aprobar(id, mid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazar(@RequestHeader("Authorization") String auth,
            @PathVariable Integer id,
            @RequestBody ModeracionReq req) {
        var mid = jwt.getUserIdFromHeader(auth);
        service.rechazar(id, mid, req.motivo());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/historial")
    public Page<ModProductoDTO> historial(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {

        Integer mid = jwt.getUserIdFromHeader(auth);
        Pageable p = PageRequest.of(pagina, tamanio, Sort.by("id").descending());
        return service.historialDe(mid, p);
    }

}