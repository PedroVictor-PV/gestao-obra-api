package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query("""
            SELECT m FROM MovimentacaoEstoque m
            JOIN FETCH m.obra
            JOIN FETCH m.material
            LEFT JOIN FETCH m.fornecedor
            WHERE m.obra.id = :obraId
            ORDER BY m.dataMovimentacao DESC, m.criadoEm DESC
            """)
    List<MovimentacaoEstoque> findAllByObraIdWithRelations(Long obraId);
}
