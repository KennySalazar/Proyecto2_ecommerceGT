package com.ecommerce.gt.ecommerce_gt.auth;

import com.ecommerce.gt.ecommerce_gt.auth.dto.LoginRequest;
import com.ecommerce.gt.ecommerce_gt.auth.dto.LoginResponse;
import com.ecommerce.gt.ecommerce_gt.auth.dto.RegisterRequest;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.RolRepository;
import com.ecommerce.gt.ecommerce_gt.usuario.repository.UsuarioRepository;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Rol;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public AuthService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario u = usuarioRepository
                .login(request.getCorreo(), request.getPassword())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        String token = UUID.randomUUID().toString();

        String rolCodigo = (u.getRol() != null) ? u.getRol().getCodigo() : null;
        return new LoginResponse(u.getId(), u.getNombre(), u.getCorreo(), rolCodigo, token);
    }

    public Usuario registerCommon(RegisterRequest req) {
        // valida email único
        if (usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        // rol COMUN
        Rol rolComun = rolRepository.findByCodigo("COMUN")
                .orElseThrow(() -> new IllegalStateException("Rol COMUN no existe, inserta seed en tabla roles"));

        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setCorreo(req.getCorreo());
        u.setTelefono(req.getTelefono());
        u.setHashPassword(md5(req.getPassword()));
        u.setRol(rolComun);
        u.setEstaActivo(true);

        return usuarioRepository.save(u);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
