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

/**
 * SERVICIO DE ADMINISTRACIÓN DE EMPLEADOS/USUARIOS.
 * CONTIENE LA LÓGICA DE NEGOCIO PARA CREAR, LISTAR, OBTENER, ACTUALIZAR
 * Y CAMBIAR EL ESTADO DE USUARIOS CON ROLES ADMINISTRATIVOS.
 *
 */
@Service
@RequiredArgsConstructor
public class AdministradorService {

    /**
     * REPOSITORIO DE USUARIOS PARA OPERACIONES CRUD.
     */
    private final UsuarioRepository usuarioRepositorio;

    /**
     * REPOSITORIO DE ROLES PARA RESOLVER Y VALIDAR CÓDIGOS DE ROL.
     */
    private final RolRepository rolRepositorio;

    /**
     * CREA UN EMPLEADO NUEVO VALIDANDO CORREO Y ROL.
     * ROLES VÁLIDOS PARA EMPLEADO: MODERADOR, LOGISTICA, ADMIN.
     *
     * @param req DTO CON NOMBRE, CORREO, TELÉFONO, ROL Y CONTRASEÑA.
     * @return DTO EmpleadoResponse CON DATOS DEL USUARIO CREADO.
     * @throws IllegalArgumentException SI EL ROL NO ES VÁLIDO O EL CORREO YA
     *                                  EXISTE.
     * @throws IllegalStateException    SI EL ROL NO SE ENCUENTRA EN LA BASE DE
     *                                  DATOS.
     */
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

    /**
     * LISTA EMPLEADOS/USUARIOS CON PAGINACIÓN Y FILTROS OPCIONALES.
     * PRIORIDAD DE FILTROS:
     * 1) SI HAY ROL Y NOMBRE: APLICA AMBOS.
     * 2) SI SOLO HAY ROL: FILTRA POR ROL.
     * 3) SI SOLO HAY NOMBRE: BUSCA EN CONJUNTO DE ROLES PERMITIDOS.
     * 4) SIN FILTROS: DEVUELVE TODOS LOS ROLES PERMITIDOS.
     * ROLES CONSIDERADOS: MODERADOR, LOGISTICA, ADMIN, COMUN.
     *
     * @param pagina  ÍNDICE DE PÁGINA (0-BASED).
     * @param tamanio TAMAÑO DE PÁGINA.
     * @param nombre  CADENA OPCIONAL CONTENIDA EN NOMBRE (CASE-INSENSITIVE).
     * @param rol     CÓDIGO DE ROL OPCIONAL; IGNORA "TODOS".
     * @return PÁGINA DE EmpleadoResponse ORDENADA DESC POR ID.
     */
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

    /**
     * CAMBIA EL ESTADO ACTIVO/INACTIVO DE UN USUARIO.
     * SOLO PERMITE ESTADOS PARA ROLES: MODERADOR, LOGISTICA, ADMIN, COMUN.
     *
     * @param id     ID DEL USUARIO A MODIFICAR.
     * @param activo NUEVO ESTADO (TRUE=ACTIVO, FALSE=INACTIVO).
     * @throws IllegalArgumentException SI EL ROL DEL USUARIO NO ESTÁ PERMITIDO.
     */
    public void cambiarEstado(Integer id, boolean activo) {
        Usuario u = usuarioRepositorio.findById(id).orElseThrow();
        if (!List.of("MODERADOR", "LOGISTICA", "ADMIN", "COMUN").contains(u.getRol().getCodigo())) {
            throw new IllegalArgumentException("El usuario no pertenece al personal administrativo");
        }
        u.setEstaActivo(activo);
        usuarioRepositorio.save(u);
    }

    /**
     * LISTA USUARIOS CON ROL COMUN CON PAGINACIÓN.
     *
     * @param pagina  ÍNDICE DE PÁGINA (0-BASED).
     * @param tamanio TAMAÑO DE PÁGINA.
     * @return PÁGINA DE ENTIDADES Usuario CON ROL "COMUN".
     */
    public Page<Usuario> listarComunes(int pagina, int tamanio) {
        Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("id").descending());
        Page<Usuario> comunes = usuarioRepositorio.findByRol_Codigo("COMUN", pageable);
        return comunes;
    }

    /**
     * OBTIENE LOS DATOS DE UN EMPLEADO POR ID Y LOS MAPEa A EmpleadoResponse.
     *
     * @param id IDENTIFICADOR DEL USUARIO.
     * @return DTO EmpleadoResponse CON LOS CAMPOS PRINCIPALES.
     * @throws java.util.NoSuchElementException SI NO EXISTE EL USUARIO.
     */
    public EmpleadoResponse obtenerEmpleado(Integer id) {
        var u = usuarioRepositorio.findById(id).orElseThrow();
        return new EmpleadoResponse(
                u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(),
                u.getRol().getCodigo(), u.getEstaActivo());
    }

    /**
     * ACTUALIZA CAMPOS EDITABLES DEL USUARIO: NOMBRE, CORREO, TELÉFONO Y,
     * SI NO ES COMUN, PERMITE CAMBIO DE ROL A (ADMIN, MODERADOR, LOGISTICA).
     * NO MODIFICA CONTRASEÑA.
     *
     * @param id  ID DEL USUARIO A ACTUALIZAR.
     * @param req DTO CON CAMPOS OPCIONALES A ACTUALIZAR.
     * @throws IllegalArgumentException SI EL NUEVO ROL ES INVÁLIDO.
     * @throws IllegalStateException    SI EL NUEVO ROL NO EXISTE EN BD.
     */
    public void actualizarEmpleado(Integer id, EmpleadoActualizarRequest req) {
        var u = usuarioRepositorio.findById(id).orElseThrow();

        if (req.getNombre() != null)
            u.setNombre(req.getNombre());
        if (req.getCorreo() != null)
            u.setCorreo(req.getCorreo());
        if (req.getTelefono() != null)
            u.setTelefono(req.getTelefono());

        if (!"COMUN".equalsIgnoreCase(u.getRol().getCodigo())) {
            if (req.getRolCodigo() != null && !req.getRolCodigo().isBlank()) {
                if (!List.of("ADMIN", "MODERADOR", "LOGISTICA").contains(req.getRolCodigo())) {
                    throw new IllegalArgumentException("Rol inválido");
                }
                var nuevoRol = rolRepositorio.findByCodigo(req.getRolCodigo())
                        .orElseThrow(() -> new IllegalStateException("No existe el rol: " + req.getRolCodigo()));
                u.setRol(nuevoRol);
            }
        }

        usuarioRepositorio.save(u);
    }
}
