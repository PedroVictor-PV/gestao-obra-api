package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.StatusServicoRequest;
import com.example.gestaoobraapi.exception.CodigoAlreadyExistsException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.StatusServicoMapper;
import com.example.gestaoobraapi.model.StatusServico;
import com.example.gestaoobraapi.repository.StatusServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StatusServicoService {
    private final StatusServicoRepository repository;
    private final StatusServicoMapper mapper;

    public StatusServicoService(StatusServicoRepository repository, StatusServicoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public StatusServico criar(StatusServico entity) {
        if (repository.findByCodigo(entity.getCodigo()).isPresent()) {
            throw new CodigoAlreadyExistsException("O código '" + entity.getCodigo() + "' já está em uso.");
        }
        return repository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<StatusServico> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public StatusServico buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status de serviço não encontrado com id: " + id));
    }

    @Transactional
    public StatusServico atualizar(Long id, StatusServicoRequest request) {
        StatusServico entity = buscarPorId(id);
        if (repository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new CodigoAlreadyExistsException("O código '" + request.getCodigo() + "' já está em uso.");
        }
        mapper.updateFromRequest(request, entity);
        return repository.save(entity);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }
}
