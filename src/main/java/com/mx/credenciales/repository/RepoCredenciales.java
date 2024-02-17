package com.mx.credenciales.repository;

import com.mx.credenciales.entity.Credenciales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoCredenciales extends JpaRepository<Credenciales,Long> {
    default Credenciales findByIdOrNull(Long id) {
        Optional<Credenciales> result = findById(id);
        return result.orElse(null);
    }

}
