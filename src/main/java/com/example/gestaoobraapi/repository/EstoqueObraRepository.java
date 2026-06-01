package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.EstoqueObra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueObraRepository extends JpaRepository<EstoqueObra, Long> {

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstoqueObra e "
            + "WHERE e.obra.id = :obraId AND e.material.id = :materialId AND e.fornecedor.id = :fornecedorId "
            + "AND e.id <> :id")
    boolean existsByObraIdAndMaterialIdAndFornecedorIdAndIdNot(
            Long obraId, Long materialId, Long fornecedorId, Long id);

    @Query("SELECT e FROM EstoqueObra e JOIN FETCH e.obra JOIN FETCH e.material JOIN FETCH e.fornecedor")
    List<EstoqueObra> findAllWithObraAndMaterial();

    @Query("""
            SELECT e FROM EstoqueObra e
            JOIN FETCH e.obra
            JOIN FETCH e.material m
            JOIN FETCH m.fornecedor
            JOIN FETCH e.fornecedor
            WHERE e.id = :id
            """)
    Optional<EstoqueObra> findByIdWithObraAndMaterial(Long id);

    @Query("""
            SELECT e FROM EstoqueObra e
            JOIN FETCH e.obra
            JOIN FETCH e.material m
            JOIN FETCH m.fornecedor
            JOIN FETCH e.fornecedor
            WHERE e.obra.id = :obraId AND m.id = :materialId AND e.fornecedor.id = :fornecedorId
            """)
    Optional<EstoqueObra> findByObraIdAndMaterialIdAndFornecedorIdWithRelations(
            Long obraId, Long materialId, Long fornecedorId);

    @Query("""
            SELECT e FROM EstoqueObra e
            JOIN FETCH e.obra
            JOIN FETCH e.material m
            JOIN FETCH m.fornecedor
            JOIN FETCH e.fornecedor
            WHERE e.obra.id = :obraId
            ORDER BY m.nome
            """)
    List<EstoqueObra> findAllByObraIdWithRelations(Long obraId);
}
