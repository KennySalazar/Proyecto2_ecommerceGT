package com.ecommerce.gt.ecommerce_gt.auth;

import com.ecommerce.gt.ecommerce_gt.auth.dto.LoginRequest;
import com.ecommerce.gt.ecommerce_gt.auth.dto.LoginResponse;
import com.ecommerce.gt.ecommerce_gt.auth.dto.RegisterRequest;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Rol;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.RolRepository;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.UsuarioRepository;
import com.ecommerce.gt.ecommerce_gt.seguridad.JwtUtil;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * SERVICIO DE AUTENTICACIÓN.
 * MANEJA EL INICIO DE SESIÓN Y EL REGISTRO DE NUEVOS USUARIOS.
 */
@Service
public class AuthService {

    // REPOSITORIO PARA ACCEDER A LOS USUARIOS EN BASE DE DATOS
    private final UsuarioRepository usuarioRepository;

    // REPOSITORIO PARA OBTENER INFORMACIÓN DE LOS ROLES
    private final RolRepository rolRepository;

    // UTILIDAD PARA GENERAR Y VALIDAR TOKENS JWT
    private final JwtUtil jwtUtil;

    // CONSTRUCTOR QUE INYECTA LOS REPOSITORIOS Y LA UTILIDAD JWT
    public AuthService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * INICIA SESIÓN DE UN USUARIO.
     * VALIDA LAS CREDENCIALES Y DEVUELVE UN TOKEN JWT JUNTO A LOS DATOS DEL
     * USUARIO.
     *
     * @param req DATOS DEL LOGIN (CORREO Y CONTRASEÑA)
     * @return LoginResponse CON TOKEN Y DATOS BÁSICOS DEL USUARIO
     */
    public LoginResponse login(LoginRequest req) {

        // SE BUSCA EL USUARIO SEGÚN SUS CREDENCIALES
        var u = usuarioRepository.login(req.getCorreo(), req.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        // SE GENERA UN TOKEN JWT CON INFORMACIÓN DEL USUARIO
        String token = jwtUtil.generarToken(
                u.getCorreo(),
                Map.of(
                        "rol", u.getRol().getCodigo(),
                        "nombre", u.getNombre(),
                        "userId", u.getId()));

        // SE DEVUELVE LA RESPUESTA CON TOKEN Y DATOS BÁSICOS
        return new LoginResponse(token, u.getNombre(), u.getRol().getCodigo());
    }

    /**
     * REGISTRA UN NUEVO USUARIO CON ROL "COMUN".
     * VERIFICA QUE EL CORREO NO EXISTA Y GUARDA EL USUARIO EN LA BASE DE DATOS.
     *
     * @param req DATOS DEL NUEVO USUARIO
     * @return ENTIDAD Usuario CREADA
     */
    public Usuario registerCommon(RegisterRequest req) {
        // VERIFICA SI EL CORREO YA EXISTE
        if (usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        // BUSCA EL ROL "COMUN" EN LA BASE DE DATOS
        Rol rolComun = rolRepository.findByCodigo("COMUN")
                .orElseThrow(() -> new IllegalStateException("Rol COMUN no existe"));

        // CREA EL NUEVO USUARIO
        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setCorreo(req.getCorreo());
        u.setTelefono(req.getTelefono());

        // ENCRIPTA LA CONTRASEÑA
        u.setHashPassword(org.springframework.util.DigestUtils
                .md5DigestAsHex(req.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8)));

        // ASIGNA ROL Y ESTADO
        u.setRol(rolComun);
        u.setEstaActivo(true);

        // GUARDA EL USUARIO EN BASE DE DATOS
        return usuarioRepository.save(u);
    }
}
