package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    @Query("""
            SELECT s FROM Servico s
            JOIN FETCH s.obra
            JOIN FETCH s.statusServico
            """)
    List<Servico> findAllWithRelations();

    @Query("""
            SELECT s FROM Servico s
            JOIN FETCH s.obra
            JOIN FETCH s.statusServico
            WHERE s.id = :id
            """)
    Optional<Servico> findByIdWithRelations(Long id);
}
