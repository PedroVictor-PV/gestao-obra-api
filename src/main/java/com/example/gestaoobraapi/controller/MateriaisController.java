package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.api.MateriaisApi;
import com.example.gestaoobraapi.dto.CategoriaMaterialRequest;
import com.example.gestaoobraapi.dto.CategoriaMaterialResponse;
import com.example.gestaoobraapi.dto.MaterialRequest;
import com.example.gestaoobraapi.dto.MaterialResponse;
import com.example.gestaoobraapi.mapper.CategoriaMaterialMapper;
import com.example.gestaoobraapi.mapper.MaterialMapper;
import com.example.gestaoobraapi.model.CategoriaMaterial;
import com.example.gestaoobraapi.model.Material;
import com.example.gestaoobraapi.service.CategoriaMaterialService;
import com.example.gestaoobraapi.service.MaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MateriaisController implements MateriaisApi {

    private final CategoriaMaterialService categoriaMaterialService;
    private final CategoriaMaterialMapper categoriaMaterialMapper;
    private final MaterialService materialService;
    private final MaterialMapper materialMapper;

    public MateriaisController(
            CategoriaMaterialService categoriaMaterialService,
            CategoriaMaterialMapper categoriaMaterialMapper,
            MaterialService materialService,
            MaterialMapper materialMapper) {
        this.categoriaMaterialService = categoriaMaterialService;
        this.categoriaMaterialMapper = categoriaMaterialMapper;
        this.materialService = materialService;
        this.materialMapper = materialMapper;
    }

    @Override
    public ResponseEntity<CategoriaMaterialResponse> criarCategoriaMaterial(CategoriaMaterialRequest request) {
        CategoriaMaterial categoria = categoriaMaterialMapper.toModel(request);
        CategoriaMaterial salva = categoriaMaterialService.criar(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaMaterialMapper.toResponse(salva));
    }

    @Override
    public ResponseEntity<List<CategoriaMaterialResponse>> listarCategoriasMaterial() {
        List<CategoriaMaterialResponse> response = categoriaMaterialService.listarTodas().stream()
                .map(categoriaMaterialMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CategoriaMaterialResponse> buscarCategoriaMaterialPorId(Long id) {
        CategoriaMaterial categoria = categoriaMaterialService.buscarPorId(id);
        return ResponseEntity.ok(categoriaMaterialMapper.toResponse(categoria));
    }

    @Override
    public ResponseEntity<CategoriaMaterialResponse> atualizarCategoriaMaterial(Long id, CategoriaMaterialRequest request) {
        CategoriaMaterial categoria = categoriaMaterialService.atualizar(id, request);
        return ResponseEntity.ok(categoriaMaterialMapper.toResponse(categoria));
    }

    @Override
    public ResponseEntity<Void> excluirCategoriaMaterial(Long id) {
        categoriaMaterialService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<MaterialResponse> criarMaterial(MaterialRequest request) {
        Material material = materialMapper.toModel(request);
        Material salvo = materialService.criar(material);
        return ResponseEntity.status(HttpStatus.CREATED).body(materialMapper.toResponse(salvo));
    }

    @Override
    public ResponseEntity<List<MaterialResponse>> listarMateriais() {
        List<MaterialResponse> response = materialService.listarTodos().stream()
                .map(materialMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MaterialResponse> buscarMaterialPorId(Long id) {
        Material material = materialService.buscarPorId(id);
        return ResponseEntity.ok(materialMapper.toResponse(material));
    }

    @Override
    public ResponseEntity<MaterialResponse> atualizarMaterial(Long id, MaterialRequest request) {
        Material material = materialService.atualizar(id, request);
        return ResponseEntity.ok(materialMapper.toResponse(material));
    }

    @Override
    public ResponseEntity<Void> excluirMaterial(Long id) {
        materialService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
