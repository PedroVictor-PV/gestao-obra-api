package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.FornecedorRequest;
import com.example.gestaoobraapi.exception.CodigoAlreadyExistsException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.FornecedorMapper;
import com.example.gestaoobraapi.model.Fornecedor;
import com.example.gestaoobraapi.repository.FornecedorRepository;
import com.example.gestaoobraapi.repository.ObraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;
    private final ObraRepository obraRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository, FornecedorMapper fornecedorMapper, ObraRepository obraRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.fornecedorMapper = fornecedorMapper;
        this.obraRepository = obraRepository;
    }

    @Transactional
    public Fornecedor criar(Fornecedor fornecedor) {
        if (fornecedorRepository.findByCodigo(fornecedor.getCodigo()).isPresent()) {
            throw new CodigoAlreadyExistsException("O código '" + fornecedor.getCodigo() + "' já está em uso.");
        }
        if (fornecedor.getObras() == null || fornecedor.getObras().isEmpty()) {
            throw new IllegalArgumentException("Ao menos uma obra vinculada ao fornecedor é obrigatória.");
        }
        List<com.example.gestaoobraapi.model.Obra> obras = fornecedor.getObras().stream()
                .map(o -> obraRepository.findById(o.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + o.getId())))
                .toList();
        fornecedor.setObras(new java.util.ArrayList<>(obras));
        return fornecedorRepository.save(fornecedor);
    }

    @Transactional(readOnly = true)
    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Fornecedor buscarPorId(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com id: " + id));
    }

    @Transactional
    public Fornecedor atualizar(Long id, FornecedorRequest request) {
        Fornecedor fornecedor = buscarPorId(id);
        if (fornecedorRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new CodigoAlreadyExistsException("O código '" + request.getCodigo() + "' já está em uso.");
        }
        fornecedorMapper.updateFromRequest(request, fornecedor);
        if (request.getIdsObras() != null && !request.getIdsObras().isEmpty()) {
            List<com.example.gestaoobraapi.model.Obra> obras = request.getIdsObras().stream()
                    .map(idObra -> obraRepository.findById(idObra)
                            .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + idObra)))
                    .toList();
            fornecedor.setObras(new java.util.ArrayList<>(obras));
        } else {
            throw new IllegalArgumentException("Ao menos uma obra vinculada ao fornecedor é obrigatória.");
        }
        return fornecedorRepository.save(fornecedor);
    }

    @Transactional
    public void excluir(Long id) {
        Fornecedor fornecedor = buscarPorId(id);
        fornecedorRepository.delete(fornecedor);
    }
}
