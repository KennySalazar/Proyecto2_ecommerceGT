package com.ecommerce.gt.ecommerce_gt.producto;

import com.ecommerce.gt.ecommerce_gt.producto.dto.*;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * CONTROLADOR DE PRODUCTOS.
 * PERMITE LISTAR PRODUCTOS PÚBLICOS, GESTIONAR MIS PRODUCTOS (DEL VENDEDOR)
 * Y CREAR/ACTUALIZAR
 * RUTA BASE: /api/productos
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    /** SERVICIO PRINCIPAL DE PRODUCTOS */
    private final ProductoService service;

    /** UTILIDAD JWT PARA OBTENER EL ID DEL USUARIO DESDE EL TOKEN */
    private final JwtUtil jwt;

    /**
     * LISTA PRODUCTOS DISPONIBLES PARA TODO PÚBLICO.
     *
     * MÉTODO: GET /api/productos/publico
     *
     * @param pagina  NÚMERO DE PÁGINA
     * @param tamanio TAMAÑO DE PÁGINA
     * @return PÁGINA DE PRODUCTOResponse
     */
    @GetMapping("/publico")
    public ResponseEntity<Page<ProductoResponse>> publico(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio) {
        return ResponseEntity.ok(service.listarPublico(pagina, tamanio));
    }

    /**
     * LISTA MIS PRODUCTOS (DEL VENDEDOR AUTENTICADO).
     *
     * MÉTODO: GET /api/productos/mis
     *
     * @param auth    CABECERA AUTHORIZATION CON TOKEN JWT
     * @param pagina  NÚMERO DE PÁGINA (POR DEFECTO 0)
     * @param tamanio TAMAÑO DE PÁGINA (POR DEFECTO 10)
     * @return PÁGINA DE PRODUCTOResponse
     */
    @GetMapping("/mis")
    public ResponseEntity<Page<ProductoResponse>> mis(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        Integer userId = jwt.getUserIdFromHeader(auth);
        return ResponseEntity.ok(service.listarMisProductos(userId, pagina, tamanio));
    }

    /**
     * CREA UN PRODUCTO DEL VENDEDOR. ACEPTA DATOS Y UNA IMAGEN OPCIONAL.
     * 
     * @param auth   TOKEN JWT DEL USUARIO
     * @param dto    DATOS DEL PRODUCTO A CREAR
     * @param imagen IMAGEN OPCIONAL DEL PRODUCTO
     * @return PRODUCTO CREADO
     */
    @PostMapping(value = "/mis", consumes = { "multipart/form-data" })
    public ResponseEntity<ProductoResponse> crear(
            @RequestHeader("Authorization") String auth,
            @RequestPart("dto") ProductoCrearRequest dto,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        Integer userId = jwt.getUserIdFromHeader(auth);
        return ResponseEntity.ok(service.crear(userId, dto, imagen));
    }

    /**
     * ACTUALIZA UN PRODUCTO DEL VENDEDOR. PERMITE CAMBIAR DATOS E IMAGEN.
     *
     *
     * @param auth        TOKEN JWT DEL USUARIO
     * @param id          ID DEL PRODUCTO A ACTUALIZAR
     * @param dto         DATOS A ACTUALIZAR
     * @param nuevaImagen IMAGEN NUEVA
     * @return PRODUCTO ACTUALIZADO
     */
    @PutMapping(value = "/mis/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<ProductoResponse> actualizar(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer id,
            @RequestPart("dto") ProductoActualizarRequest dto,
            @RequestPart(value = "imagen", required = false) MultipartFile nuevaImagen) {
        Integer userId = jwt.getUserIdFromHeader(auth);
        return ResponseEntity.ok(service.actualizar(id, userId, dto, nuevaImagen));
    }

    /**
     * OBTIENE UN PRODUCTO PROPIO POR ID (VALIDA PROPIEDAD).
     *
     * MÉTODO: GET /api/productos/mis/{id}
     *
     * @param auth TOKEN JWT DEL USUARIO
     * @param id   ID DEL PRODUCTO
     * @return DETALLE DEL PRODUCTO DEL VENDEDOR
     */
    @GetMapping("/mis/{id}")
    public ResponseEntity<ProductoResponse> obtenerMio(
            @RequestHeader("Authorization") String auth,
            @PathVariable Integer id) {
        Integer userId = jwt.getUserIdFromHeader(auth);
        return ResponseEntity.ok(service.obtenerMio(userId, id));
    }
}
