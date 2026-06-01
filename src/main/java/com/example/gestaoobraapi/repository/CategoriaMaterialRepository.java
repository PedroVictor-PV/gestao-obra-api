package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.CategoriaMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaMaterialRepository extends JpaRepository<CategoriaMaterial, Long> {

    Optional<CategoriaMaterial> findByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
