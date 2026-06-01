package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @Query("SELECT m FROM Material m JOIN FETCH m.categoria JOIN FETCH m.fornecedor")
    List<Material> findAllWithCategoriaAndFornecedor();

    @Query("SELECT m FROM Material m JOIN FETCH m.categoria JOIN FETCH m.fornecedor WHERE m.id = :id")
    Optional<Material> findByIdWithCategoriaAndFornecedor(Long id);
}
