package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.api.PerfisApi;
import com.example.gestaoobraapi.dto.PerfilResponse;
import com.example.gestaoobraapi.mapper.PerfilMapper;
import com.example.gestaoobraapi.model.Perfil;
import com.example.gestaoobraapi.service.PerfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PerfilController implements PerfisApi {

    private final PerfilService perfilService;
    private final PerfilMapper perfilMapper;

    public PerfilController(PerfilService perfilService, PerfilMapper perfilMapper) {
        this.perfilService = perfilService;
        this.perfilMapper = perfilMapper;
    }

    @Override
    public ResponseEntity<List<PerfilResponse>> listarPerfis() {
        List<PerfilResponse> response = perfilService.listarTodos().stream()
                .map(perfilMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PerfilResponse> buscarPerfilPorId(Long id) {
        Perfil perfil = perfilService.buscarPorId(id);
        return ResponseEntity.ok(perfilMapper.toResponse(perfil));
    }
}
