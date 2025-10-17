package com.ecommerce.gt.ecommerce_gt.admin;

import com.ecommerce.gt.ecommerce_gt.admin.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/empleados")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdministradorController {

    private final AdministradorService servicio;

    @PostMapping
    public ResponseEntity<EmpleadoResponse> crear(@RequestBody EmpleadoCrearRequest req) {
        return ResponseEntity.ok(servicio.crearEmpleado(req));
    }

    @GetMapping
    public ResponseEntity<Page<EmpleadoResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanio) {
        return ResponseEntity.ok(servicio.listarEmpleados(pagina, tamanio));
    }

    @GetMapping("/contadores")
    public ResponseEntity<EmpleadoContadores> contadores() {
        return ResponseEntity.ok(servicio.obtenerContadores());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Integer id, @RequestParam boolean activo) {
        servicio.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build();
    }

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
}