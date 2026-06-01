package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.model.*;
import com.example.gestaoobraapi.repository.MovimentacaoEstoqueRepository;
import com.example.gestaoobraapi.repository.ObraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final ObraRepository obraRepository;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            ObraRepository obraRepository) {
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.obraRepository = obraRepository;
    }

    @Transactional
    public MovimentacaoEstoque registrar(
            Obra obra,
            Material material,
            TipoMovimentacao tipo,
            BigDecimal quantidade,
            String observacao) {
        MovimentacaoEstoque movimentacao = MovimentacaoEstoque.builder()
                .obra(obra)
                .material(material)
                .fornecedor(material.getFornecedor())
                .tipo(tipo)
                .quantidade(quantidade)
                .dataMovimentacao(LocalDate.now())
                .observacao(observacao)
                .build();
        return movimentacaoEstoqueRepository.save(movimentacao);
    }

    @Transactional
    public void registrarEntradaEstoque(
            EstoqueObra estoqueObra,
            BigDecimal quantidadeEntrada,
            String observacao) {
        registrar(
                estoqueObra.getObra(),
                estoqueObra.getMaterial(),
                TipoMovimentacao.ENTRADA,
                quantidadeEntrada,
                observacao != null ? observacao : "Entrada de material na obra"
        );
    }

    @Transactional
    public void registrarAjusteEstoque(
            EstoqueObra estoqueObra,
            BigDecimal quantidadeAnterior,
            BigDecimal quantidadeNova,
            String observacao) {
        if (quantidadeAnterior.compareTo(quantidadeNova) == 0) {
            return;
        }
        registrar(
                estoqueObra.getObra(),
                estoqueObra.getMaterial(),
                TipoMovimentacao.AJUSTE,
                quantidadeNova,
                observacao != null ? observacao : "Ajuste de quantidade em estoque na obra"
        );
    }

    @Transactional
    public void registrarSaidaEstoque(
            EstoqueObra estoqueObra,
            BigDecimal quantidadeSaida,
            String observacao) {
        registrar(
                estoqueObra.getObra(),
                estoqueObra.getMaterial(),
                TipoMovimentacao.SAIDA,
                quantidadeSaida,
                observacao != null ? observacao : "Remoção de material da obra"
        );
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoque> listarPorObra(Long obraId) {
        if (!obraRepository.existsById(obraId)) {
            throw new ResourceNotFoundException("Obra não encontrada com id: " + obraId);
        }
        return movimentacaoEstoqueRepository.findAllByObraIdWithRelations(obraId);
    }
}
