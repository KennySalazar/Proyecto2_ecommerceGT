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

/**
 * CONTROLADOR PARA MODERADORES.
 * PERMITE VER SOLICITUDES PENDIENTES, APROBAR, RECHAZAR
 * Y VER EL HISTORIAL DEL MODERADOR AUTENTICADO.
 *
 * RUTA BASE: /api/moderador
 */
@RestController
@RequestMapping("/api/moderador")
@RequiredArgsConstructor
public class ModeradorController {

    /** SERVICIO CON LA LÓGICA DE MODERACIÓN */
    private final ModeracionService service;

    /** UTILIDAD JWT PARA OBTENER EL ID DEL USUARIO DESDE EL TOKEN */
    private final JwtUtil jwt;

    /**
     * LISTA PRODUCTOS PENDIENTES DE MODERACIÓN.
     *
     * MÉTODO: GET /api/moderador/solicitudes
     *
     * @param pagina  NÚMERO DE PÁGINA
     * @param tamanio TAMAÑO DE LA PÁGINA
     * @return PÁGINA DE PRODUCTOS PENDIENTES
     */
    @GetMapping("/solicitudes")
    public Page<ProductoModeracionDTO> pendientes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {
        return service.listarPendientes(pagina, tamanio);
    }

    /**
     * APRUEBA UN PRODUCTO PENDIENTE.
     *
     * MÉTODO: POST /api/moderador/solicitudes/{id}/aprobar
     *
     * @param auth CABECERA AUTHORIZATION CON TOKEN JWT
     * @param id   ID DEL PRODUCTO A APROBAR
     * @return 200 OK SI TODO SALE BIEN
     */
    @PostMapping("/solicitudes/{id}/aprobar")
    public ResponseEntity<?> aprobar(@RequestHeader("Authorization") String auth,
            @PathVariable Integer id) {
        var mid = jwt.getUserIdFromHeader(auth);
        service.aprobar(id, mid);
        return ResponseEntity.ok().build();
    }

    /**
     * RECHAZA UN PRODUCTO PENDIENTE INDICANDO UN MOTIVO.
     *
     * MÉTODO: POST /api/moderador/solicitudes/{id}/rechazar
     *
     * @param auth CABECERA AUTHORIZATION CON TOKEN JWT
     * @param id   ID DEL PRODUCTO A RECHAZAR
     * @param req  CUERPO CON EL MOTIVO DE RECHAZO
     * @return 200 OK SI SE RECHAZA CORRECTAMENTE
     */
    @PostMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazar(@RequestHeader("Authorization") String auth,
            @PathVariable Integer id,
            @RequestBody ModeracionReq req) {
        var mid = jwt.getUserIdFromHeader(auth);
        service.rechazar(id, mid, req.motivo());
        return ResponseEntity.ok().build();
    }

    /**
     * MUESTRA EL HISTORIAL DE MODERACIONES DEL MODERADOR AUTENTICADO.
     *
     * MÉTODO: GET /api/moderador/historial
     *
     * @param auth    CABECERA AUTHORIZATION CON TOKEN JWT
     * @param pagina  NÚMERO DE PÁGINA
     * @param tamanio TAMAÑO DE LA PÁGINA
     * @return PÁGINA DE HISTORIAL (APROBADOS/RECHAZADOS) ORDENADO DESC POR ID
     */
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
