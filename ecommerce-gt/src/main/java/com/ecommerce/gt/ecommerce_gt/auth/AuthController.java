package com.ecommerce.gt.ecommerce_gt.auth;

import com.ecommerce.gt.ecommerce_gt.auth.dto.LoginRequest;
import com.ecommerce.gt.ecommerce_gt.auth.dto.LoginResponse;
import com.ecommerce.gt.ecommerce_gt.auth.dto.RegisterRequest;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            Usuario u = service.registerCommon(req);
            return ResponseEntity.ok(new Object() {
                public final Integer id = u.getId();
                public final String nombre = u.getNombre();
                public final String correo = u.getCorreo();
                public final String rol = u.getRol().getCodigo();
            });
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new Object() {
                public final String message = ex.getMessage();
            });
        }
    }
}
