package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.FornecedorRequest;
import com.example.gestaoobraapi.exception.CodigoAlreadyExistsException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.FornecedorMapper;
import com.example.gestaoobraapi.model.Fornecedor;
import com.example.gestaoobraapi.repository.FornecedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final FornecedorMapper fornecedorMapper;

    public FornecedorService(FornecedorRepository fornecedorRepository, FornecedorMapper fornecedorMapper) {
        this.fornecedorRepository = fornecedorRepository;
        this.fornecedorMapper = fornecedorMapper;
    }

    @Transactional
    public Fornecedor criar(Fornecedor fornecedor) {
        if (fornecedorRepository.findByCodigo(fornecedor.getCodigo()).isPresent()) {
            throw new CodigoAlreadyExistsException("O código '" + fornecedor.getCodigo() + "' já está em uso.");
        }
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
        return fornecedorRepository.save(fornecedor);
    }

    @Transactional
    public void excluir(Long id) {
        Fornecedor fornecedor = buscarPorId(id);
        fornecedorRepository.delete(fornecedor);
    }
}
