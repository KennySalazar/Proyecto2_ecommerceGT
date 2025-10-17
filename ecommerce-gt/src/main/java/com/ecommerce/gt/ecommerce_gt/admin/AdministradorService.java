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

    public Page<EmpleadoResponse> listarEmpleados(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("id").descending());
        Page<Usuario> empleados = usuarioRepositorio
                .findByRol_CodigoIn(List.of("MODERADOR", "LOGISTICA", "ADMIN", "COMUN"), pageable);

        return empleados.map(u -> new EmpleadoResponse(
                u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(),
                u.getRol().getCodigo(), u.getEstaActivo()));
    }

    public EmpleadoContadores obtenerContadores() {
        long total = usuarioRepositorio.countByRol_CodigoIn(List.of("MODERADOR", "LOGISTICA", "ADMIN", "COMUN"));
        long moderadores = usuarioRepositorio.countByRol_Codigo("MODERADOR");
        long logistica = usuarioRepositorio.countByRol_Codigo("LOGISTICA");
        long administradores = usuarioRepositorio.countByRol_Codigo("ADMIN");
        Long comunes = usuarioRepositorio.countByRol_Codigo("COMUN");
        return new EmpleadoContadores(total, moderadores, logistica, administradores, comunes);
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
        // Puedes crear un método en repo: Page<Usuario> findByRol_Codigo(String codigo,
        // Pageable p);
        Page<Usuario> comunes = usuarioRepositorio.findByRol_Codigo("COMUN", pageable);
        return comunes;
    }
}