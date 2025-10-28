package com.ecommerce.gt.ecommerce_gt.carrito.controller;

import com.ecommerce.gt.ecommerce_gt.carrito.dto.GananciaResumenDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.dto.GananciaItemDTO;
import com.ecommerce.gt.ecommerce_gt.carrito.service.GananciasService;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * CONTROLADOR DE GANANCIAS DEL VENDEDOR.
 * PERMITE CONSULTAR UN RESUMEN GENERAL DE GANANCIAS
 * Y LISTAR LAS GANANCIAS DETALLADAS POR PEDIDO O PRODUCTO.
 * 
 * RUTA BASE: /api/ganancias
 */
@RestController
@RequestMapping("/api/ganancias")
@RequiredArgsConstructor
public class GananciasController {

    // SERVICIO QUE CONTIENE LA LÓGICA DE CÁLCULO DE GANANCIAS
    private final GananciasService svc;

    // UTILIDAD PARA OBTENER DATOS DEL USUARIO AUTENTICADO DESDE EL TOKEN JWT
    private final JwtUtil jwt;

    /**
     * OBTIENE UN RESUMEN GENERAL DE LAS GANANCIAS DEL VENDEDOR.
     * INCLUYE INFORMACIÓN COMO TOTAL GANADO, PEDIDOS ENTREGADOS, ETC.
     *
     * MÉTODO: GET /api/ganancias/resumen
     *
     * @param auth TOKEN DE AUTORIZACIÓN DEL VENDEDOR.
     * @return OBJETO GananciaResumenDTO CON LOS DATOS RESUMIDOS.
     */
    @GetMapping("/resumen")
    public GananciaResumenDTO resumen(@RequestHeader("Authorization") String auth) {
        Integer vendedorId = jwt.getUserIdFromHeader(auth);
        return svc.resumen(vendedorId);
    }

    /**
     * LISTA LAS GANANCIAS DETALLADAS DEL VENDEDOR CON PAGINACIÓN.
     * CADA REGISTRO REPRESENTA UNA VENTA O PRODUCTO VENDIDO.
     *
     * MÉTODO: GET /api/ganancias
     *
     * @param auth    TOKEN DE AUTORIZACIÓN DEL VENDEDOR.
     * @param pagina  NÚMERO DE PÁGINA A CONSULTAR (POR DEFECTO 0).
     * @param tamanio TAMAÑO DE LA PÁGINA (POR DEFECTO 12).
     * @return PÁGINA DE GananciaItemDTO CON EL DETALLE DE LAS GANANCIAS.
     */
    @GetMapping
    public Page<GananciaItemDTO> listar(@RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {
        Integer vendedorId = jwt.getUserIdFromHeader(auth);
        return svc.listar(vendedorId, pagina, tamanio);
    }
}
