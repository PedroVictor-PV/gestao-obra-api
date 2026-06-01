package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.api.ObraApi;
import com.example.gestaoobraapi.dto.EstoqueObraRequest;
import com.example.gestaoobraapi.dto.EstoqueObraResponse;
import com.example.gestaoobraapi.dto.MovimentacaoEstoqueResponse;
import com.example.gestaoobraapi.dto.ObraRequest;
import com.example.gestaoobraapi.dto.ObraResponse;
import com.example.gestaoobraapi.dto.StatusObraRequest;
import com.example.gestaoobraapi.dto.StatusObraResponse;
import com.example.gestaoobraapi.mapper.EstoqueObraMapper;
import com.example.gestaoobraapi.mapper.MovimentacaoEstoqueMapper;
import com.example.gestaoobraapi.mapper.ObraMapper;
import com.example.gestaoobraapi.mapper.StatusObraMapper;
import com.example.gestaoobraapi.model.EstoqueObra;
import com.example.gestaoobraapi.model.Obra;
import com.example.gestaoobraapi.model.StatusObra;
import com.example.gestaoobraapi.service.EstoqueObraService;
import com.example.gestaoobraapi.service.MovimentacaoEstoqueService;
import com.example.gestaoobraapi.service.ObraService;
import com.example.gestaoobraapi.service.StatusObraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ObraController implements ObraApi {

    private final ObraService obraService;
    private final ObraMapper obraMapper;
    private final StatusObraService statusObraService;
    private final StatusObraMapper statusObraMapper;
    private final EstoqueObraService estoqueObraService;
    private final EstoqueObraMapper estoqueObraMapper;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final MovimentacaoEstoqueMapper movimentacaoEstoqueMapper;

    public ObraController(
            ObraService obraService,
            ObraMapper obraMapper,
            StatusObraService statusObraService,
            StatusObraMapper statusObraMapper,
            EstoqueObraService estoqueObraService,
            EstoqueObraMapper estoqueObraMapper,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            MovimentacaoEstoqueMapper movimentacaoEstoqueMapper) {
        this.obraService = obraService;
        this.obraMapper = obraMapper;
        this.statusObraService = statusObraService;
        this.statusObraMapper = statusObraMapper;
        this.estoqueObraService = estoqueObraService;
        this.estoqueObraMapper = estoqueObraMapper;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.movimentacaoEstoqueMapper = movimentacaoEstoqueMapper;
    }

    @Override
    public ResponseEntity<ObraResponse> criarObra(ObraRequest obraRequest) {
        Obra obra = obraMapper.toModel(obraRequest);
        Obra obraSalva = obraService.criar(obra);
        return ResponseEntity.status(HttpStatus.CREATED).body(obraMapper.toResponse(obraSalva));
    }

    @Override
    public ResponseEntity<List<ObraResponse>> listarObras() {
        List<ObraResponse> response = obraService.listarTodas().stream()
                .map(obraMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ObraResponse> buscarObraPorId(Long id) {
        Obra obra = obraService.buscarPorId(id);
        return ResponseEntity.ok(obraMapper.toResponse(obra));
    }

    @Override
    public ResponseEntity<ObraResponse> atualizarObra(Long id, ObraRequest obraRequest) {
        Obra obra = obraService.atualizar(id, obraRequest);
        return ResponseEntity.ok(obraMapper.toResponse(obra));
    }

    @Override
    public ResponseEntity<Void> excluirObra(Long id) {
        obraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<StatusObraResponse> criarStatusObra(StatusObraRequest statusObraRequest) {
        StatusObra statusObra = statusObraMapper.toModel(statusObraRequest);
        StatusObra statusObraSalvo = statusObraService.criar(statusObra);
        return ResponseEntity.status(HttpStatus.CREATED).body(statusObraMapper.toResponse(statusObraSalvo));
    }

    @Override
    public ResponseEntity<List<StatusObraResponse>> listarStatusObra() {
        List<StatusObraResponse> response = statusObraService.listarTodos().stream()
                .map(statusObraMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<StatusObraResponse> buscarStatusObraPorId(Long id) {
        StatusObra statusObra = statusObraService.buscarPorId(id);
        return ResponseEntity.ok(statusObraMapper.toResponse(statusObra));
    }

    @Override
    public ResponseEntity<StatusObraResponse> atualizarStatusObra(Long id, StatusObraRequest statusObraRequest) {
        StatusObra statusObra = statusObraService.atualizar(id, statusObraRequest);
        return ResponseEntity.ok(statusObraMapper.toResponse(statusObra));
    }

    @Override
    public ResponseEntity<Void> excluirStatusObra(Long id) {
        statusObraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EstoqueObraResponse> criarEstoqueObra(EstoqueObraRequest estoqueObraRequest) {
        EstoqueObra estoqueObra = estoqueObraMapper.toModel(estoqueObraRequest);
        EstoqueObra estoqueSalvo = estoqueObraService.criar(estoqueObra, estoqueObraRequest.getObservacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(estoqueObraMapper.toResponse(estoqueSalvo));
    }

    @Override
    public ResponseEntity<List<EstoqueObraResponse>> listarEstoqueObra() {
        List<EstoqueObraResponse> response = estoqueObraService.listarTodos().stream()
                .map(estoqueObraMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<EstoqueObraResponse> buscarEstoqueObraPorId(Long id) {
        EstoqueObra estoqueObra = estoqueObraService.buscarPorId(id);
        return ResponseEntity.ok(estoqueObraMapper.toResponse(estoqueObra));
    }

    @Override
    public ResponseEntity<EstoqueObraResponse> atualizarEstoqueObra(Long id, EstoqueObraRequest estoqueObraRequest) {
        EstoqueObra estoqueObra = estoqueObraService.atualizar(id, estoqueObraRequest);
        return ResponseEntity.ok(estoqueObraMapper.toResponse(estoqueObra));
    }

    @Override
    public ResponseEntity<Void> excluirEstoqueObra(Long id) {
        estoqueObraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> listarMovimentacoesEstoquePorObra(Long idObra) {
        List<MovimentacaoEstoqueResponse> response = movimentacaoEstoqueService.listarPorObra(idObra).stream()
                .map(movimentacaoEstoqueMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
