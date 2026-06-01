package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.CategoriaMaterialRequest;
import com.example.gestaoobraapi.exception.CodigoAlreadyExistsException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.CategoriaMaterialMapper;
import com.example.gestaoobraapi.model.CategoriaMaterial;
import com.example.gestaoobraapi.repository.CategoriaMaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaMaterialService {

    private final CategoriaMaterialRepository categoriaMaterialRepository;
    private final CategoriaMaterialMapper categoriaMaterialMapper;

    public CategoriaMaterialService(
            CategoriaMaterialRepository categoriaMaterialRepository,
            CategoriaMaterialMapper categoriaMaterialMapper) {
        this.categoriaMaterialRepository = categoriaMaterialRepository;
        this.categoriaMaterialMapper = categoriaMaterialMapper;
    }

    @Transactional
    public CategoriaMaterial criar(CategoriaMaterial categoriaMaterial) {
        if (categoriaMaterialRepository.findByCodigo(categoriaMaterial.getCodigo()).isPresent()) {
            throw new CodigoAlreadyExistsException("O código '" + categoriaMaterial.getCodigo() + "' já está em uso.");
        }
        return categoriaMaterialRepository.save(categoriaMaterial);
    }

    @Transactional(readOnly = true)
    public List<CategoriaMaterial> listarTodas() {
        return categoriaMaterialRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CategoriaMaterial buscarPorId(Long id) {
        return categoriaMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de material não encontrada com id: " + id));
    }

    @Transactional
    public CategoriaMaterial atualizar(Long id, CategoriaMaterialRequest request) {
        CategoriaMaterial categoriaMaterial = buscarPorId(id);
        if (categoriaMaterialRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new CodigoAlreadyExistsException("O código '" + request.getCodigo() + "' já está em uso.");
        }
        categoriaMaterialMapper.updateFromRequest(request, categoriaMaterial);
        return categoriaMaterialRepository.save(categoriaMaterial);
    }

    @Transactional
    public void excluir(Long id) {
        CategoriaMaterial categoriaMaterial = buscarPorId(id);
        categoriaMaterialRepository.delete(categoriaMaterial);
    }
}
