package com.ecommerce.gt.ecommerce_gt.carrito.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.AddItemReq;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.CarritoDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.CheckoutReq;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.UpdateItemReq;
import com.ecommerce.gt.ecommerce_gt.carrito.entity.TarjetaGuardada;
import com.ecommerce.gt.ecommerce_gt.carrito.repository.PedidoRepository;
import com.ecommerce.gt.ecommerce_gt.carrito.service.CarritoService;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * CONTROLADOR DEL CARRITO DE COMPRAS.
 * PERMITE VER, AGREGAR, MODIFICAR Y ELIMINAR PRODUCTOS DEL CARRITO.
 * TAMBIÉN GESTIONA EL PROCESO DE COMPRA (CHECKOUT) Y LAS TARJETAS GUARDADAS.
 * 
 * RUTA BASE: /api/carrito
 */
@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    // SERVICIO PRINCIPAL QUE CONTIENE LA LÓGICA DEL CARRITO
    private final CarritoService service;

    // UTILIDAD PARA LEER DATOS DEL TOKEN JWT
    private final JwtUtil jwt;

    // REPOSITORIO DE PEDIDOS (USADO EN PROCESO DE COMPRA)
    private final PedidoRepository pedidoRepo;

    /**
     * EXTRAE EL ID DEL USUARIO AUTENTICADO DESDE EL TOKEN DE AUTORIZACIÓN.
     *
     * @param auth CABECERA "Authorization" CON EL TOKEN JWT.
     * @return ID DEL USUARIO AUTENTICADO.
     */
    private Integer userId(String auth) {
        return jwt.getUserIdFromHeader(auth);
    }

    /**
     * MUESTRA EL CARRITO ACTUAL DEL USUARIO.
     *
     * MÉTODO: GET /api/carrito
     *
     * @param auth TOKEN DE AUTORIZACIÓN DEL USUARIO.
     * @return OBJETO CarritoDTO CON LOS PRODUCTOS Y TOTALES DEL CARRITO.
     */
    @GetMapping
    public CarritoDTO ver(@RequestHeader("Authorization") String auth) {
        return service.ver(userId(auth));
    }

    /**
     * AGREGA UN PRODUCTO AL CARRITO DEL USUARIO.
     *
     * MÉTODO: POST /api/carrito/agregar
     *
     * @param auth TOKEN DE AUTORIZACIÓN DEL USUARIO.
     * @param req  DATOS DEL PRODUCTO A AGREGAR (ID Y CANTIDAD).
     * @return CARRITO ACTUALIZADO.
     */
    @PostMapping("/agregar")
    public CarritoDTO agregar(@RequestHeader("Authorization") String auth,
            @RequestBody AddItemReq req) {
        return service.agregar(userId(auth), req.getProductoId(), req.getCantidad());
    }

    /**
     * ACTUALIZA LA CANTIDAD DE UN PRODUCTO EN EL CARRITO.
     *
     * MÉTODO: PUT /api/carrito/item
     *
     * @param auth TOKEN DE AUTORIZACIÓN DEL USUARIO.
     * @param req  DATOS DEL PRODUCTO Y NUEVA CANTIDAD.
     * @return CARRITO ACTUALIZADO.
     */
    @PutMapping("/item")
    public CarritoDTO actualizar(@RequestHeader("Authorization") String auth,
            @RequestBody UpdateItemReq req) {
        return service.actualizarCantidad(userId(auth), req.getProductoId(), req.getCantidad());
    }

    /**
     * ELIMINA UN PRODUCTO ESPECÍFICO DEL CARRITO.
     *
     * MÉTODO: DELETE /api/carrito/item/{productoId}
     *
     * @param auth       TOKEN DE AUTORIZACIÓN DEL USUARIO.
     * @param productoId ID DEL PRODUCTO A ELIMINAR.
     * @return CARRITO ACTUALIZADO SIN EL PRODUCTO.
     */
    @DeleteMapping("/item/{productoId}")
    public CarritoDTO eliminarItem(@RequestHeader("Authorization") String auth,
            @PathVariable Integer productoId) {
        return service.eliminarItem(userId(auth), productoId);
    }

    /**
     * VACÍA TODO EL CARRITO DEL USUARIO.
     *
     * MÉTODO: DELETE /api/carrito
     *
     * @param auth TOKEN DE AUTORIZACIÓN DEL USUARIO.
     */
    @DeleteMapping
    public void vaciar(@RequestHeader("Authorization") String auth) {
        service.vaciar(userId(auth));
    }

    /**
     * REALIZA EL PROCESO DE COMPRA (CHECKOUT).
     * CREA UN PEDIDO A PARTIR DEL CARRITO ACTUAL Y LO GUARDA EN LA BASE DE DATOS.
     *
     * MÉTODO: POST /api/carrito/checkout
     *
     * @param auth TOKEN DE AUTORIZACIÓN DEL USUARIO.
     * @param req  DATOS DEL PAGO Y ENVÍO.
     * @return ID DEL PEDIDO CREADO O MENSAJE DE ERROR SI FALLA.
     */
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

    /**
     * OBTIENE LAS TARJETAS GUARDADAS DEL USUARIO.
     *
     * MÉTODO: GET /api/carrito/tarjetas
     *
     * @param auth TOKEN DE AUTORIZACIÓN DEL USUARIO.
     * @return LISTA DE TARJETAS ASOCIADAS AL USUARIO.
     */
    @GetMapping("/tarjetas")
    public List<TarjetaGuardada> tarjetas(@RequestHeader("Authorization") String auth) {
        return service.tarjetasDe(userId(auth));
    }
}
