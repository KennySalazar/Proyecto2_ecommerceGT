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

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Autentica y devuelve JWT + datos básicos.
     */
    public LoginResponse login(LoginRequest req) {

        var u = usuarioRepository.login(req.getCorreo(), req.getPassword())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        String token = jwtUtil.generarToken(
                u.getCorreo(),
                Map.of(
                        "rol", u.getRol().getCodigo(),
                        "nombre", u.getNombre(),
                        "userId", u.getId()));

        return new LoginResponse(token, u.getNombre(), u.getRol().getCodigo());
    }

    /**
     * Registro de usuario COMUN.
     */
    public Usuario registerCommon(RegisterRequest req) {
        if (usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        Rol rolComun = rolRepository.findByCodigo("COMUN")
                .orElseThrow(() -> new IllegalStateException("Rol COMUN no existe"));

        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setCorreo(req.getCorreo());
        u.setTelefono(req.getTelefono());
        u.setHashPassword(org.springframework.util.DigestUtils
                .md5DigestAsHex(req.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        u.setRol(rolComun);
        u.setEstaActivo(true);

        return usuarioRepository.save(u);
    }
}
