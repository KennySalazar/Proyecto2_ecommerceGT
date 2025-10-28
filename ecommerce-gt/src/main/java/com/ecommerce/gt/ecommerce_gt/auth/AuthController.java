package com.ecommerce.gt.ecommerce_gt.auth;

import com.ecommerce.gt.ecommerce_gt.auth.dto.LoginRequest;
import com.ecommerce.gt.ecommerce_gt.auth.dto.LoginResponse;
import com.ecommerce.gt.ecommerce_gt.auth.dto.RegisterRequest;
import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CONTROLADOR DE AUTENTICACIÓN DEL SISTEMA.
 * SE ENCARGA DE MANEJAR EL INICIO DE SESIÓN Y EL REGISTRO DE NUEVOS USUARIOS.
 * 
 * RUTA BASE: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    /**
     * SERVICIO DE AUTENTICACIÓN DONDE SE ENCUENTRA LA LÓGICA DE LOGIN Y REGISTRO.
     */
    private final AuthService service;

    /**
     * CONSTRUCTOR QUE INYECTA EL SERVICIO DE AUTENTICACIÓN.
     *
     * @param service Servicio encargado de manejar la lógica de autenticación.
     */
    public AuthController(AuthService service) {
        this.service = service;
    }

    /**
     * PERMITE INICIAR SESIÓN EN EL SISTEMA.
     * RECIBE LAS CREDENCIALES DEL USUARIO (CORREO Y CONTRASEÑA),
     * Y SI SON CORRECTAS, DEVUELVE UN TOKEN Y DATOS BÁSICOS DEL USUARIO.
     *
     * MÉTODO: POST /api/auth/login
     *
     * @param request Objeto con los datos del inicio de sesión.
     * @return Respuesta con los datos del usuario autenticado y el token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    /**
     * PERMITE REGISTRAR UN NUEVO USUARIO COMÚN EN EL SISTEMA.
     * SI LOS DATOS SON VÁLIDOS, CREA UN NUEVO USUARIO Y DEVUELVE SUS DATOS
     * PRINCIPALES.
     * SI HAY UN ERROR (POR EJEMPLO, CORREO YA REGISTRADO), DEVUELVE UN MENSAJE DE
     * ERROR.
     *
     * MÉTODO: POST /api/auth/register
     *
     * @param req Datos del usuario que se desea registrar (nombre, correo,
     *            contraseña, etc.).
     * @return Respuesta con el usuario creado o un mensaje de error si algo falla.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            Usuario u = service.registerCommon(req);
            // Se devuelve un objeto anónimo con los datos del nuevo usuario.
            return ResponseEntity.ok(new Object() {
                public final Integer id = u.getId();
                public final String nombre = u.getNombre();
                public final String correo = u.getCorreo();
                public final String rol = u.getRol().getCodigo();
            });
        } catch (IllegalArgumentException ex) {
            // Si ocurre un error, se devuelve un mensaje con la descripción.
            return ResponseEntity.badRequest().body(new Object() {
                public final String message = ex.getMessage();
            });
        }
    }
}
