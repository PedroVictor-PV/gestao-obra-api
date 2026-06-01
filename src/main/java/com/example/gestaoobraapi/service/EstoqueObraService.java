package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.EstoqueObraRequest;
import com.example.gestaoobraapi.exception.BusinessException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.EstoqueObraMapper;
import com.example.gestaoobraapi.model.EstoqueObra;
import com.example.gestaoobraapi.model.Material;
import com.example.gestaoobraapi.model.Obra;
import com.example.gestaoobraapi.repository.EstoqueObraRepository;
import com.example.gestaoobraapi.repository.MaterialRepository;
import com.example.gestaoobraapi.repository.ObraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueObraService {

    private final EstoqueObraRepository estoqueObraRepository;
    private final ObraRepository obraRepository;
    private final MaterialRepository materialRepository;
    private final EstoqueObraMapper estoqueObraMapper;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public EstoqueObraService(
            EstoqueObraRepository estoqueObraRepository,
            ObraRepository obraRepository,
            MaterialRepository materialRepository,
            EstoqueObraMapper estoqueObraMapper,
            MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.estoqueObraRepository = estoqueObraRepository;
        this.obraRepository = obraRepository;
        this.materialRepository = materialRepository;
        this.estoqueObraMapper = estoqueObraMapper;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @Transactional
    public EstoqueObra criar(EstoqueObra estoqueObra, String observacao) {
        Long obraId = estoqueObra.getObra().getId();
        Long materialId = estoqueObra.getMaterial().getId();
        BigDecimal quantidadeEntrada = estoqueObra.getQuantidadeAtual();

        Obra obra = buscarObra(obraId);
        Material material = buscarMaterialComFornecedor(materialId);
        Long fornecedorId = material.getFornecedor().getId();

        Optional<EstoqueObra> estoqueExistente = estoqueObraRepository
                .findByObraIdAndMaterialIdAndFornecedorIdWithRelations(obraId, materialId, fornecedorId);

        if (estoqueExistente.isPresent()) {
            return consolidarEntrada(
                    estoqueExistente.get(), quantidadeEntrada, estoqueObra.getQuantidadeMinima(), observacao);
        }

        estoqueObra.setObra(obra);
        estoqueObra.setMaterial(material);
        estoqueObra.setFornecedor(material.getFornecedor());
        EstoqueObra estoqueSalvo = estoqueObraRepository.save(estoqueObra);
        EstoqueObra estoqueCompleto = estoqueObraRepository.findByIdWithObraAndMaterial(estoqueSalvo.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado com id: " + estoqueSalvo.getId()));
        movimentacaoEstoqueService.registrarEntradaEstoque(estoqueCompleto, quantidadeEntrada, observacao);
        return estoqueCompleto;
    }

    @Transactional(readOnly = true)
    public List<EstoqueObra> listarTodos() {
        return estoqueObraRepository.findAllWithObraAndMaterial();
    }

    @Transactional(readOnly = true)
    public EstoqueObra buscarPorId(Long id) {
        return estoqueObraRepository.findByIdWithObraAndMaterial(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado com id: " + id));
    }

    @Transactional
    public EstoqueObra atualizar(Long id, EstoqueObraRequest request) {
        EstoqueObra estoqueObra = buscarPorId(id);
        BigDecimal quantidadeAnterior = estoqueObra.getQuantidadeAtual();
        Material material = buscarMaterialComFornecedor(request.getIdMaterial());
        Long fornecedorId = material.getFornecedor().getId();

        if (estoqueObraRepository.existsByObraIdAndMaterialIdAndFornecedorIdAndIdNot(
                request.getIdObra(), request.getIdMaterial(), fornecedorId, id)) {
            throw new BusinessException(
                    "Já existe estoque cadastrado para esta obra, material e fornecedor.");
        }
        estoqueObraMapper.updateFromRequest(request, estoqueObra);
        estoqueObra.setObra(buscarObra(request.getIdObra()));
        estoqueObra.setMaterial(material);
        estoqueObra.setFornecedor(material.getFornecedor());
        EstoqueObra estoqueAtualizado = estoqueObraRepository.save(estoqueObra);
        movimentacaoEstoqueService.registrarAjusteEstoque(
                estoqueAtualizado,
                quantidadeAnterior,
                estoqueAtualizado.getQuantidadeAtual(),
                request.getObservacao());
        return estoqueAtualizado;
    }

    @Transactional
    public void excluir(Long id) {
        EstoqueObra estoqueObra = buscarPorId(id);
        movimentacaoEstoqueService.registrarSaidaEstoque(estoqueObra, estoqueObra.getQuantidadeAtual(), null);
        estoqueObraRepository.delete(estoqueObra);
    }

    private EstoqueObra consolidarEntrada(
            EstoqueObra estoqueExistente,
            BigDecimal quantidadeEntrada,
            BigDecimal quantidadeMinima,
            String observacao) {
        BigDecimal quantidadeAtualizada = estoqueExistente.getQuantidadeAtual().add(quantidadeEntrada);
        estoqueExistente.setQuantidadeAtual(quantidadeAtualizada);
        if (quantidadeMinima != null) {
            estoqueExistente.setQuantidadeMinima(quantidadeMinima);
        }

        EstoqueObra estoqueAtualizado = estoqueObraRepository.save(estoqueExistente);
        movimentacaoEstoqueService.registrarEntradaEstoque(estoqueAtualizado, quantidadeEntrada, observacao);
        return estoqueAtualizado;
    }

    private Obra buscarObra(Long id) {
        return obraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + id));
    }

    private Material buscarMaterialComFornecedor(Long id) {
        return materialRepository.findByIdWithCategoriaAndFornecedor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado com id: " + id));
    }
}
