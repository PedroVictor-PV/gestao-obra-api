package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.api.ServicoApi;
import com.example.gestaoobraapi.dto.*;
import com.example.gestaoobraapi.mapper.*;
import com.example.gestaoobraapi.model.*;
import com.example.gestaoobraapi.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServicoController implements ServicoApi {

    private final StatusServicoService statusServicoService;
    private final StatusServicoMapper statusServicoMapper;
    private final ServicoService servicoService;
    private final ServicoMapper servicoMapper;
    private final ServicoMaterialService servicoMaterialService;
    private final ServicoMaterialMapper servicoMaterialMapper;

    public ServicoController(
            StatusServicoService statusServicoService,
            StatusServicoMapper statusServicoMapper,
            ServicoService servicoService,
            ServicoMapper servicoMapper,
            ServicoMaterialService servicoMaterialService,
            ServicoMaterialMapper servicoMaterialMapper) {
        this.statusServicoService = statusServicoService;
        this.statusServicoMapper = statusServicoMapper;
        this.servicoService = servicoService;
        this.servicoMapper = servicoMapper;
        this.servicoMaterialService = servicoMaterialService;
        this.servicoMaterialMapper = servicoMaterialMapper;
    }

    @Override
    public ResponseEntity<StatusServicoResponse> criarStatusServico(StatusServicoRequest request) {
        StatusServico salvo = statusServicoService.criar(statusServicoMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(statusServicoMapper.toResponse(salvo));
    }

    @Override
    public ResponseEntity<List<StatusServicoResponse>> listarStatusServico() {
        return ResponseEntity.ok(statusServicoService.listarTodos().stream()
                .map(statusServicoMapper::toResponse).toList());
    }

    @Override
    public ResponseEntity<StatusServicoResponse> buscarStatusServicoPorId(Long id) {
        return ResponseEntity.ok(statusServicoMapper.toResponse(statusServicoService.buscarPorId(id)));
    }

    @Override
    public ResponseEntity<StatusServicoResponse> atualizarStatusServico(Long id, StatusServicoRequest request) {
        return ResponseEntity.ok(statusServicoMapper.toResponse(statusServicoService.atualizar(id, request)));
    }

    @Override
    public ResponseEntity<Void> excluirStatusServico(Long id) {
        statusServicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ServicoResponse> criarServico(ServicoRequest request) {
        Servico salvo = servicoService.criar(servicoMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoMapper.toResponse(salvo));
    }

    @Override
    public ResponseEntity<List<ServicoResponse>> listarServicos() {
        return ResponseEntity.ok(servicoService.listarTodos().stream()
                .map(servicoMapper::toResponse).toList());
    }

    @Override
    public ResponseEntity<ServicoResponse> buscarServicoPorId(Long id) {
        return ResponseEntity.ok(servicoMapper.toResponse(servicoService.buscarPorId(id)));
    }

    @Override
    public ResponseEntity<ServicoResponse> atualizarServico(Long id, ServicoRequest request) {
        return ResponseEntity.ok(servicoMapper.toResponse(servicoService.atualizar(id, request)));
    }

    @Override
    public ResponseEntity<Void> excluirServico(Long id) {
        servicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ServicoMaterialResponse> criarServicoMaterial(ServicoMaterialRequest request) {
        ServicoMaterial salvo = servicoMaterialService.criar(servicoMaterialMapper.toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoMaterialMapper.toResponse(salvo));
    }

    @Override
    public ResponseEntity<List<ServicoMaterialResponse>> listarServicoMaterial() {
        return ResponseEntity.ok(servicoMaterialService.listarTodos().stream()
                .map(servicoMaterialMapper::toResponse).toList());
    }

    @Override
    public ResponseEntity<ServicoMaterialResponse> buscarServicoMaterialPorId(Long id) {
        return ResponseEntity.ok(servicoMaterialMapper.toResponse(servicoMaterialService.buscarPorId(id)));
    }

    @Override
    public ResponseEntity<ServicoMaterialResponse> atualizarServicoMaterial(Long id, ServicoMaterialRequest request) {
        return ResponseEntity.ok(servicoMaterialMapper.toResponse(servicoMaterialService.atualizar(id, request)));
    }

    @Override
    public ResponseEntity<Void> excluirServicoMaterial(Long id) {
        servicoMaterialService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
