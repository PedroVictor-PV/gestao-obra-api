package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.api.PerfilPermissoesApi;
import com.example.gestaoobraapi.dto.PerfilPermissaoResponse;
import com.example.gestaoobraapi.mapper.PerfilPermissaoMapper;
import com.example.gestaoobraapi.service.PerfilPermissaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PerfilPermissaoController implements PerfilPermissoesApi {

    private final PerfilPermissaoService perfilPermissaoService;
    private final PerfilPermissaoMapper perfilPermissaoMapper;

    public PerfilPermissaoController(
            PerfilPermissaoService perfilPermissaoService,
            PerfilPermissaoMapper perfilPermissaoMapper) {
        this.perfilPermissaoService = perfilPermissaoService;
        this.perfilPermissaoMapper = perfilPermissaoMapper;
    }

    @Override
    public ResponseEntity<List<PerfilPermissaoResponse>> listarPerfilPermissoes() {
        List<PerfilPermissaoResponse> response = perfilPermissaoService.listarTodas().stream()
                .map(perfilPermissaoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
