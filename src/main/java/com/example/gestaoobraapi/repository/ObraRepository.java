package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ObraRepository extends JpaRepository<Obra, Long> {

    Optional<Obra> findByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @Query("SELECT o FROM Obra o JOIN FETCH o.statusObra LEFT JOIN FETCH o.responsavel")
    List<Obra> findAllWithStatusAndResponsavel();

    @Query("SELECT o FROM Obra o JOIN FETCH o.statusObra LEFT JOIN FETCH o.responsavel WHERE o.id = :id")
    Optional<Obra> findByIdWithStatusAndResponsavel(Long id);
}
