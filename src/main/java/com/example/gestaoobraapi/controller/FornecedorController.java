package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.api.FornecedoresApi;
import com.example.gestaoobraapi.dto.FornecedorRequest;
import com.example.gestaoobraapi.dto.FornecedorResponse;
import com.example.gestaoobraapi.mapper.FornecedorMapper;
import com.example.gestaoobraapi.model.Fornecedor;
import com.example.gestaoobraapi.service.FornecedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FornecedorController implements FornecedoresApi {

    private final FornecedorService fornecedorService;
    private final FornecedorMapper fornecedorMapper;

    public FornecedorController(FornecedorService fornecedorService, FornecedorMapper fornecedorMapper) {
        this.fornecedorService = fornecedorService;
        this.fornecedorMapper = fornecedorMapper;
    }

    @Override
    public ResponseEntity<FornecedorResponse> criarFornecedor(FornecedorRequest fornecedorRequest) {
        Fornecedor fornecedor = fornecedorMapper.toModel(fornecedorRequest);
        Fornecedor fornecedorSalvo = fornecedorService.criar(fornecedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorMapper.toResponse(fornecedorSalvo));
    }

    @Override
    public ResponseEntity<List<FornecedorResponse>> listarFornecedores() {
        List<FornecedorResponse> response = fornecedorService.listarTodos().stream()
                .map(fornecedorMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<FornecedorResponse> buscarFornecedorPorId(Long id) {
        Fornecedor fornecedor = fornecedorService.buscarPorId(id);
        return ResponseEntity.ok(fornecedorMapper.toResponse(fornecedor));
    }

    @Override
    public ResponseEntity<FornecedorResponse> atualizarFornecedor(Long id, FornecedorRequest fornecedorRequest) {
        Fornecedor fornecedor = fornecedorService.atualizar(id, fornecedorRequest);
        return ResponseEntity.ok(fornecedorMapper.toResponse(fornecedor));
    }

    @Override
    public ResponseEntity<Void> excluirFornecedor(Long id) {
        fornecedorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
