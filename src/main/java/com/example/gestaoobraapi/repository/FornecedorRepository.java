package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    Optional<Fornecedor> findByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
