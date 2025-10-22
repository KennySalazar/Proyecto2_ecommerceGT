package com.ecommerce.gt.ecommerce_gt.admin;

import com.ecommerce.gt.ecommerce_gt.admin.dto.*;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Rol;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.RolRepository;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final UsuarioRepository usuarioRepositorio;
    private final RolRepository rolRepositorio;

    public EmpleadoResponse crearEmpleado(EmpleadoCrearRequest req) {
        if (!List.of("MODERADOR", "LOGISTICA", "ADMIN").contains(req.getRolCodigo())) {
            throw new IllegalArgumentException("Rol inválido para empleado");
        }
        if (usuarioRepositorio.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Rol rol = rolRepositorio.findByCodigo(req.getRolCodigo())
                .orElseThrow(() -> new IllegalStateException("No existe el rol: " + req.getRolCodigo()));

        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setCorreo(req.getCorreo());
        u.setTelefono(req.getTelefono());
        u.setEstaActivo(true);
        u.setRol(rol);

        u.setHashPassword(DigestUtils.md5DigestAsHex(req.getContrasena().getBytes()));

        u = usuarioRepositorio.save(u);

        return new EmpleadoResponse(
                u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(), u.getRol().getCodigo(), u.getEstaActivo());
    }

    public Page<EmpleadoResponse> listarEmpleados(int pagina, int tamanio, String nombre, String rol) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("id").descending());

        Page<Usuario> empleados;

        boolean tieneNombre = (nombre != null && !nombre.isBlank());
        boolean tieneRol = (rol != null && !rol.isBlank() && !"TODOS".equalsIgnoreCase(rol));

        if (tieneRol && tieneNombre) {
            empleados = usuarioRepositorio.findByRol_CodigoAndNombreContainingIgnoreCase(rol, nombre, pageable);
        } else if (tieneRol) {
            empleados = usuarioRepositorio.findByRol_Codigo(rol, pageable);
        } else if (tieneNombre) {
            empleados = usuarioRepositorio.findByRol_CodigoInAndNombreContainingIgnoreCase(
                    List.of("MODERADOR", "LOGISTICA", "ADMIN", "COMUN"), nombre, pageable);
        } else {
            empleados = usuarioRepositorio.findByRol_CodigoIn(
                    List.of("MODERADOR", "LOGISTICA", "ADMIN", "COMUN"), pageable);
        }

        return empleados.map(u -> new EmpleadoResponse(
                u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(),
                u.getRol().getCodigo(), u.getEstaActivo()));
    }

    public void cambiarEstado(Integer id, boolean activo) {
        Usuario u = usuarioRepositorio.findById(id).orElseThrow();
        if (!List.of("MODERADOR", "LOGISTICA", "ADMIN", "COMUN").contains(u.getRol().getCodigo())) {
            throw new IllegalArgumentException("El usuario no pertenece al personal administrativo");
        }
        u.setEstaActivo(activo);
        usuarioRepositorio.save(u);
    }

    public Page<Usuario> listarComunes(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("id").descending());
        Page<Usuario> comunes = usuarioRepositorio.findByRol_Codigo("COMUN", pageable);
        return comunes;
    }
}
