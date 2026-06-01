package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.MovimentacaoEstoque;

import java.time.LocalDate;
import java.util.List;

public interface MovimentacaoEstoqueRepositoryCustom {

    List<MovimentacaoEstoque> findAllWithFilters(Long obraId, LocalDate dataInicio, LocalDate dataFim);
}
