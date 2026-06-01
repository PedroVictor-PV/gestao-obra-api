package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.StatusServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusServicoRepository extends JpaRepository<StatusServico, Long> {
    Optional<StatusServico> findByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
