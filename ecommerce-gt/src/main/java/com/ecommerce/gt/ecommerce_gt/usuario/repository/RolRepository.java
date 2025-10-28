package com.ecommerce.gt.ecommerce_gt.usuario.repository;

import com.ecommerce.gt.ecommerce_gt.usuario.entity.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * REPOSITORIO PARA LA ENTIDAD ROL.
 * PERMITE BUSCAR, GUARDAR Y LISTAR ROLES DE USUARIO.
 */
public interface RolRepository extends JpaRepository<Rol, Integer> {

    /**
     * BUSCA UN ROL POR SU CÓDIGO ("ADMIN", "COMUN", "MODERADOR").
     *
     * @param codigo CÓDIGO DEL ROL
     * @return UN Optional CON EL ROL SI EXISTE, O VACÍO SI NO SE ENCUENTRA
     */
    Optional<Rol> findByCodigo(String codigo);
}
