package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.StatusObraRequest;
import com.example.gestaoobraapi.exception.CodigoAlreadyExistsException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.StatusObraMapper;
import com.example.gestaoobraapi.model.StatusObra;
import com.example.gestaoobraapi.repository.StatusObraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StatusObraService {

    private final StatusObraRepository statusObraRepository;
    private final StatusObraMapper statusObraMapper;

    public StatusObraService(StatusObraRepository statusObraRepository, StatusObraMapper statusObraMapper) {
        this.statusObraRepository = statusObraRepository;
        this.statusObraMapper = statusObraMapper;
    }

    @Transactional
    public StatusObra criar(StatusObra statusObra) {
        if (statusObraRepository.findByCodigo(statusObra.getCodigo()).isPresent()) {
            throw new CodigoAlreadyExistsException("O código '" + statusObra.getCodigo() + "' já está em uso.");
        }
        return statusObraRepository.save(statusObra);
    }

    @Transactional(readOnly = true)
    public List<StatusObra> listarTodos() {
        return statusObraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public StatusObra buscarPorId(Long id) {
        return statusObraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status de obra não encontrado com id: " + id));
    }

    @Transactional
    public StatusObra atualizar(Long id, StatusObraRequest request) {
        StatusObra statusObra = buscarPorId(id);
        if (statusObraRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new CodigoAlreadyExistsException("O código '" + request.getCodigo() + "' já está em uso.");
        }
        statusObraMapper.updateFromRequest(request, statusObra);
        return statusObraRepository.save(statusObra);
    }

    @Transactional
    public void excluir(Long id) {
        StatusObra statusObra = buscarPorId(id);
        statusObraRepository.delete(statusObra);
    }
}
