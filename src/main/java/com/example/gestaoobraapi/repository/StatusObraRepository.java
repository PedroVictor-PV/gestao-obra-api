package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.StatusObra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusObraRepository extends JpaRepository<StatusObra, Long> {

    Optional<StatusObra> findByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
