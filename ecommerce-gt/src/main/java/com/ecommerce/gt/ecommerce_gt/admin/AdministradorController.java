package com.ecommerce.gt.ecommerce_gt.admin;

import com.ecommerce.gt.ecommerce_gt.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CONTROLADOR REST PARA LA GESTIÓN DE EMPLEADOS DESDE EL MÓDULO DE
 * ADMINISTRACIÓN.
 * EXPONE ENDPOINTS PARA CREAR, LISTAR, OBTENER, ACTUALIZAR Y CAMBIAR EL ESTADO
 * DE EMPLEADOS.
 * RUTA BASE: /api/admin/empleados
 */
@RestController
@RequestMapping("/api/admin/empleados")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdministradorController {

    /**
     * SERVICIO DE NEGOCIO QUE CONTIENE LA LÓGICA PARA ADMINISTRAR EMPLEADOS.
     * INYECTADO VÍA CONSTRUCTOR GENERADO POR LOMBOK (@RequiredArgsConstructor).
     */
    private final AdministradorService servicio;

    /**
     * CREA UN NUEVO EMPLEADO.
     * MÉTODO: POST /api/admin/empleados
     *
     * @param req OBJETO CON LOS DATOS NECESARIOS PARA CREAR AL EMPLEADO.
     * @return ResponseEntity CON EL EMPLEADO CREADO (DTO EmpleadoResponse).
     */
    @PostMapping
    public ResponseEntity<EmpleadoResponse> crear(@RequestBody EmpleadoCrearRequest req) {
        return ResponseEntity.ok(servicio.crearEmpleado(req));
    }

    /**
     * LISTA EMPLEADOS CON PAGINACIÓN Y FILTROS OPCIONALES DE NOMBRE Y ROL.
     * MÉTODO: GET /api/admin/empleados
     *
     * @param pagina  ÍNDICE DE PÁGINA (CERO-BASED). POR DEFECTO 0.
     * @param tamanio TAMAÑO DE PÁGINA. POR DEFECTO 10.
     * @param nombre  FILTRO OPCIONAL POR NOMBRE (CONTIENE).
     * @param rol     FILTRO OPCIONAL POR CÓDIGO DE ROL. POR DEFECTO "TODOS".
     * @return ResponseEntity CON UNA PÁGINA DE EmpleadoResponse.
     */
    @GetMapping
    public ResponseEntity<Page<EmpleadoResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false, defaultValue = "TODOS") String rol) {
        return ResponseEntity.ok(servicio.listarEmpleados(pagina, tamanio, nombre, rol));
    }

    /**
     * CAMBIA EL ESTADO ACTIVO/INACTIVO DE UN EMPLEADO SIN MODIFICAR OTROS CAMPOS.
     * MÉTODO: PATCH /api/admin/empleados/{id}/estado
     *
     * @param id     IDENTIFICADOR DEL EMPLEADO A MODIFICAR.
     * @param activo BANDERA QUE INDICA SI EL EMPLEADO QUEDARÁ ACTIVO (true) O
     *               INACTIVO (false).
     * @return ResponseEntity SIN CONTENIDO (204) SI LA OPERACIÓN ES EXITOSA.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Integer id, @RequestParam boolean activo) {
        servicio.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build();
    }

    /**
     * LISTA USUARIOS COMUNES (NO EMPLEADOS) PARA APOYO EN TAREAS ADMINISTRATIVAS.
     * DEVUELVE UNA PROYECCIÓN TIPO EmpleadoResponse MAPEADA DESDE ENTIDADES DE
     * USUARIO.
     * MÉTODO: GET /api/admin/empleados/comunes
     *
     * @param pagina  ÍNDICE DE PÁGINA (CERO-BASED). POR DEFECTO 0.
     * @param tamanio TAMAÑO DE PÁGINA. POR DEFECTO 10.
     * @return ResponseEntity CON UNA PÁGINA DE EmpleadoResponse CORRESPONDIENTE A
     *         USUARIOS COMUNES.
     */
    @GetMapping("/comunes")
    public ResponseEntity<Page<EmpleadoResponse>> listarComunes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {

        var res = servicio.listarComunes(pagina, tamanio)
                .map(u -> new EmpleadoResponse(
                        u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(),
                        u.getRol().getCodigo(), u.getEstaActivo()));
        return ResponseEntity.ok(res);
    }

    /**
     * OBTIENE EL DETALLE DE UN EMPLEADO POR SU IDENTIFICADOR.
     * MÉTODO: GET /api/admin/empleados/{id}
     *
     * @param id IDENTIFICADOR DEL EMPLEADO A CONSULTAR.
     * @return ResponseEntity CON LOS DATOS DEL EMPLEADO (DTO EmpleadoResponse).
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.obtenerEmpleado(id));
    }

    /**
     * ACTUALIZA LOS DATOS DE UN EMPLEADO EXISTENTE.
     * MÉTODO: PUT /api/admin/empleados/{id}
     *
     * @param id  IDENTIFICADOR DEL EMPLEADO A ACTUALIZAR.
     * @param req OBJETO CON LOS CAMPOS PERMITIDOS PARA ACTUALIZACIÓN.
     * @return ResponseEntity SIN CONTENIDO (204) SI LA OPERACIÓN ES EXITOSA.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Integer id,
            @RequestBody EmpleadoActualizarRequest req) {
        servicio.actualizarEmpleado(id, req);
        return ResponseEntity.noContent().build();
    }
}
