package com.ecommerce.gt.ecommerce_gt.usuario.repository;

import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO JPA PARA LOS USUARIOS DEL SISTEMA.
 * MANEJA CONSULTAS DE LOGIN, BÚSQUEDA, VALIDACIÓN Y FILTROS POR ROL.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

        /** VERIFICA SI YA EXISTE UN USUARIO CON EL CORREO DADO. */
        boolean existsByCorreo(String correo);

        /**
         * LOGIN BÁSICO: VERIFICA CORREO Y CONTRASEÑA (ENCRIPTADA CON MD5).
         * SOLO PERMITE ACCESO SI EL USUARIO ESTÁ ACTIVO.
         */
        @Query(value = """
                        SELECT * FROM ecommerce.usuarios u
                         WHERE u.correo = :correo
                           AND u.esta_activo = TRUE
                           AND u.hash_password = md5(:password)
                        """, nativeQuery = true)
        Optional<Usuario> login(@Param("correo") String correo, @Param("password") String password);

        /** LISTA USUARIOS CUYO ROL ESTÁ EN UNA LISTA DE CÓDIGOS. */
        Page<Usuario> findByRol_CodigoIn(List<String> codigos, Pageable pageable);

        /** CUENTA LOS USUARIOS QUE TIENEN UNO DE LOS ROLES DADOS. */
        long countByRol_CodigoIn(List<String> codigos);

        /** CUENTA LOS USUARIOS POR UN ROL ESPECÍFICO. */
        long countByRol_Codigo(String codigo);

        /** LISTA LOS USUARIOS DE UN ROL EN PARTICULAR CON PAGINACIÓN. */
        Page<Usuario> findByRol_Codigo(String codigo, Pageable pageable);

        /** BUSCA USUARIOS QUE COINCIDAN POR NOMBRE Y ROL (VARIOS ROLES). */
        Page<Usuario> findByRol_CodigoInAndNombreContainingIgnoreCase(
                        List<String> roles, String nombre, Pageable p);

        /** BUSCA USUARIOS POR NOMBRE DENTRO DE UN ROL ESPECÍFICO. */
        Page<Usuario> findByRol_CodigoAndNombreContainingIgnoreCase(
                        String rol, String nombre, Pageable p);

        /**
         * VERIFICA SI YA EXISTE UN CORREO, EXCLUYENDO UN ID DETERMINADO (USADO EN
         * EDICIÓN).
         */
        boolean existsByCorreoAndIdNot(String correo, Integer id);
}
