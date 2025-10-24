package com.ecommerce.gt.ecommerce_gt.carrito.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.AddItemReq;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.CarritoDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.CheckoutReq;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.UpdateItemReq;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.TarjetaGuardada;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.service.CarritoService;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {
    private final CarritoService service;
    private final JwtUtil jwt;
    private final PedidoRepository pedidoRepo;

    private Integer userId(String auth) {
        return jwt.getUserIdFromHeader(auth);
    }

    @GetMapping
    public CarritoDTO ver(@RequestHeader("Authorization") String auth) {
        return service.ver(userId(auth));
    }

    @PostMapping("/agregar")
    public CarritoDTO agregar(@RequestHeader("Authorization") String auth,
            @RequestBody AddItemReq req) {
        return service.agregar(userId(auth), req.getProductoId(), req.getCantidad());
    }

    @PutMapping("/item")
    public CarritoDTO actualizar(@RequestHeader("Authorization") String auth,
            @RequestBody UpdateItemReq req) {
        return service.actualizarCantidad(userId(auth), req.getProductoId(), req.getCantidad());
    }

    @DeleteMapping("/item/{productoId}")
    public CarritoDTO eliminarItem(@RequestHeader("Authorization") String auth,
            @PathVariable Integer productoId) {
        return service.eliminarItem(userId(auth), productoId);
    }

    @DeleteMapping
    public void vaciar(@RequestHeader("Authorization") String auth) {
        service.vaciar(userId(auth));
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestBody CheckoutReq req) {

        if (auth == null || auth.isBlank()) {
            return ResponseEntity.status(401).body(Map.of("message", "Falta token Authorization"));
        }
        System.out.println(userId(auth));
        System.out.println(req);
        try {
            var pedidoId = service.checkout(userId(auth), req);
            return ResponseEntity.ok(Map.of("pedidoId", pedidoId));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/tarjetas")
    public List<TarjetaGuardada> tarjetas(@RequestHeader("Authorization") String auth) {
        return service.tarjetasDe(userId(auth));
    }

}
