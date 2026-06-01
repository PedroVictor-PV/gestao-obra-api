package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.api.PermissoesApi;
import com.example.gestaoobraapi.dto.PermissaoResponse;
import com.example.gestaoobraapi.mapper.PermissaoMapper;
import com.example.gestaoobraapi.model.Permissao;
import com.example.gestaoobraapi.service.PermissaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PermissaoController implements PermissoesApi {

    private final PermissaoService permissaoService;
    private final PermissaoMapper permissaoMapper;

    public PermissaoController(PermissaoService permissaoService, PermissaoMapper permissaoMapper) {
        this.permissaoService = permissaoService;
        this.permissaoMapper = permissaoMapper;
    }

    @Override
    public ResponseEntity<List<PermissaoResponse>> listarPermissoes() {
        List<PermissaoResponse> response = permissaoService.listarTodas().stream()
                .map(permissaoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PermissaoResponse> buscarPermissaoPorId(Long id) {
        Permissao permissao = permissaoService.buscarPorId(id);
        return ResponseEntity.ok(permissaoMapper.toResponse(permissao));
    }
}
