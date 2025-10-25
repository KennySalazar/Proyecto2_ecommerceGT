package com.ecommerce.gt.ecommerce_gt.usuario.repository;

import com.ecommerce.gt.ecommerce_gt.usuario.entity.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByCorreo(String correo);

    @Query(value = """
            SELECT * FROM ecommerce.usuarios u
             WHERE u.correo = :correo
               AND u.esta_activo = TRUE
               AND u.hash_password = md5(:password)
            """, nativeQuery = true)
    Optional<Usuario> login(@Param("correo") String correo, @Param("password") String password);

    Page<Usuario> findByRol_CodigoIn(List<String> codigos, Pageable pageable);

    long countByRol_CodigoIn(List<String> codigos);

    long countByRol_Codigo(String codigo);

    Page<Usuario> findByRol_Codigo(String codigo, Pageable pageable);

    Page<Usuario> findByRol_CodigoInAndNombreContainingIgnoreCase(
            List<String> roles, String nombre, Pageable p);

    Page<Usuario> findByRol_CodigoAndNombreContainingIgnoreCase(
            String rol, String nombre, Pageable p);

    boolean existsByCorreoAndIdNot(String correo, Integer id);
}
