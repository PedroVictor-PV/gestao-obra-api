package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.ServicoMaterialRequest;
import com.example.gestaoobraapi.exception.BusinessException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.ServicoMaterialMapper;
import com.example.gestaoobraapi.model.*;
import com.example.gestaoobraapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicoMaterialService {

    private final ServicoMaterialRepository servicoMaterialRepository;
    private final ServicoRepository servicoRepository;
    private final MaterialRepository materialRepository;
    private final EstoqueObraRepository estoqueObraRepository;
    private final ServicoMaterialMapper mapper;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public ServicoMaterialService(
            ServicoMaterialRepository servicoMaterialRepository,
            ServicoRepository servicoRepository,
            MaterialRepository materialRepository,
            EstoqueObraRepository estoqueObraRepository,
            ServicoMaterialMapper mapper,
            MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.servicoMaterialRepository = servicoMaterialRepository;
        this.servicoRepository = servicoRepository;
        this.materialRepository = materialRepository;
        this.estoqueObraRepository = estoqueObraRepository;
        this.mapper = mapper;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @Transactional
    public ServicoMaterial criar(ServicoMaterial servicoMaterial) {
        Servico servico = servicoRepository.findByIdWithRelations(servicoMaterial.getServico().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
        Long obraId = servicoMaterial.getObra().getId();
        if (!servico.getObra().getId().equals(obraId)) {
            throw new BusinessException("A obra informada não corresponde à obra do serviço.");
        }
        Long materialId = servicoMaterial.getMaterial().getId();
        if (servicoMaterialRepository.existsByServicoIdAndObraIdAndMaterialId(
                servico.getId(), obraId, materialId)) {
            throw new BusinessException("Material já destinado a este serviço na obra.");
        }

        Material material = materialRepository.findByIdWithCategoriaAndFornecedor(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado com id: " + materialId));
        BigDecimal quantidade = servicoMaterial.getQuantidade();

        EstoqueObra estoque = estoqueObraRepository
                .findByObraIdAndMaterialIdAndFornecedorIdWithRelations(
                        obraId, materialId, material.getFornecedor().getId())
                .orElseThrow(() -> new BusinessException(
                        "Não existe estoque do material informado para esta obra."));

        if (estoque.getQuantidadeAtual().compareTo(quantidade) < 0) {
            throw new BusinessException(
                    "Quantidade insuficiente em estoque. Disponível: " + estoque.getQuantidadeAtual()
                            + ", solicitado: " + quantidade);
        }

        servicoMaterial.setServico(servico);
        servicoMaterial.setObra(servico.getObra());
        servicoMaterial.setMaterial(material);
        ServicoMaterial salvo = servicoMaterialRepository.save(servicoMaterial);

        estoque.setQuantidadeAtual(estoque.getQuantidadeAtual().subtract(quantidade));
        estoqueObraRepository.save(estoque);

        String obs = "Enviada para serviço: " + servico.getNome();
        if (servicoMaterial.getObservacao() != null && !servicoMaterial.getObservacao().isBlank()) {
            obs = obs + " - " + servicoMaterial.getObservacao();
        }
        movimentacaoEstoqueService.registrarSaidaEstoque(estoque, quantidade, obs);

        return servicoMaterialRepository.findByIdWithRelations(salvo.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinação não encontrada com id: " + salvo.getId()));
    }

    @Transactional(readOnly = true)
    public List<ServicoMaterial> listarTodos() {
        return servicoMaterialRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public ServicoMaterial buscarPorId(Long id) {
        return servicoMaterialRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destinação não encontrada com id: " + id));
    }

    @Transactional
    public ServicoMaterial atualizar(Long id, ServicoMaterialRequest request) {
        ServicoMaterial entity = buscarPorId(id);
        if (servicoMaterialRepository.existsByServicoIdAndObraIdAndMaterialIdAndIdNot(
                request.getIdServico(), request.getIdObra(), request.getIdMaterial(), id)) {
            throw new BusinessException("Material já destinado a este serviço na obra.");
        }
        mapper.updateFromRequest(request, entity);
        Servico servico = servicoRepository.findByIdWithRelations(request.getIdServico())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
        entity.setServico(servico);
        entity.setObra(servico.getObra());
        entity.setMaterial(materialRepository.findByIdWithCategoriaAndFornecedor(request.getIdMaterial())
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado")));
        return servicoMaterialRepository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        servicoMaterialRepository.delete(buscarPorId(id));
    }
}
