package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.ServicoMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServicoMaterialRepository extends JpaRepository<ServicoMaterial, Long> {

    boolean existsByServicoIdAndObraIdAndMaterialId(Long servicoId, Long obraId, Long materialId);

    boolean existsByServicoIdAndObraIdAndMaterialIdAndIdNot(Long servicoId, Long obraId, Long materialId, Long id);

    @Query("""
            SELECT sm FROM ServicoMaterial sm
            JOIN FETCH sm.servico s
            JOIN FETCH sm.obra
            JOIN FETCH sm.material m
            JOIN FETCH m.fornecedor
            """)
    List<ServicoMaterial> findAllWithRelations();

    @Query("""
            SELECT sm FROM ServicoMaterial sm
            JOIN FETCH sm.servico s
            JOIN FETCH sm.obra
            JOIN FETCH sm.material m
            JOIN FETCH m.fornecedor
            WHERE sm.id = :id
            """)
    Optional<ServicoMaterial> findByIdWithRelations(Long id);

    @Query("""
            SELECT sm FROM ServicoMaterial sm
            JOIN FETCH sm.servico s
            JOIN FETCH sm.obra
            JOIN FETCH sm.material m
            JOIN FETCH m.fornecedor
            WHERE sm.obra.id = :obraId
            ORDER BY s.nome, m.nome
            """)
    List<ServicoMaterial> findAllByObraIdWithRelations(Long obraId);
}
