package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.MaterialRequest;
import com.example.gestaoobraapi.exception.CodigoAlreadyExistsException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.MaterialMapper;
import com.example.gestaoobraapi.model.CategoriaMaterial;
import com.example.gestaoobraapi.model.Fornecedor;
import com.example.gestaoobraapi.model.Material;
import com.example.gestaoobraapi.repository.CategoriaMaterialRepository;
import com.example.gestaoobraapi.repository.FornecedorRepository;
import com.example.gestaoobraapi.repository.MaterialRepository;
import com.example.gestaoobraapi.repository.ObraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;

import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CategoriaMaterialRepository categoriaMaterialRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ObraRepository obraRepository;
    private final MaterialMapper materialMapper;

    public MaterialService(
            MaterialRepository materialRepository,
            CategoriaMaterialRepository categoriaMaterialRepository,
            FornecedorRepository fornecedorRepository,
            ObraRepository obraRepository,
            MaterialMapper materialMapper) {
        this.materialRepository = materialRepository;
        this.categoriaMaterialRepository = categoriaMaterialRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.obraRepository = obraRepository;
        this.materialMapper = materialMapper;
    }

    @Transactional
    public Material criar(MaterialRequest request) {
        if (materialRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new CodigoAlreadyExistsException("O código '" + request.getCodigo() + "' já está em uso.");
        }
        Material material = materialMapper.toModel(request);
        material.setCategoria(buscarCategoria(request.getIdCategoria()));
        material.setFornecedor(buscarFornecedor(request.getIdFornecedor()));
        material.setObras(buscarObras(request.getIdsObras()));
        Material materialSalvo = materialRepository.save(material);
        return materialRepository.findByIdWithCategoriaAndFornecedor(materialSalvo.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado com id: " + materialSalvo.getId()));
    }

    @Transactional(readOnly = true)
    public List<Material> listarTodos() {
        return materialRepository.findAllWithCategoriaAndFornecedor();
    }

    @Transactional(readOnly = true)
    public Material buscarPorId(Long id) {
        return materialRepository.findByIdWithCategoriaAndFornecedor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado com id: " + id));
    }

    @Transactional
    public Material atualizar(Long id, MaterialRequest request) {
        Material material = buscarPorId(id);
        if (materialRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new CodigoAlreadyExistsException("O código '" + request.getCodigo() + "' já está em uso.");
        }
        materialMapper.updateFromRequest(request, material);
        material.setCategoria(buscarCategoria(request.getIdCategoria()));
        material.setFornecedor(buscarFornecedor(request.getIdFornecedor()));
        material.setObras(buscarObras(request.getIdsObras()));
        return materialRepository.save(material);
    }

    @Transactional
    public void excluir(Long id) {
        Material material = buscarPorId(id);
        materialRepository.delete(material);
    }

    private CategoriaMaterial buscarCategoria(Long id) {
        return categoriaMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de material não encontrada com id: " + id));
    }

    private Fornecedor buscarFornecedor(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com id: " + id));
    }

    private java.util.Set<com.example.gestaoobraapi.model.Obra> buscarObras(List<Long> idsObras) {
        if (idsObras == null || idsObras.isEmpty()) {
            return new HashSet<>();
        }
        List<com.example.gestaoobraapi.model.Obra> obras = obraRepository.findAllById(idsObras);
        if (obras.size() != idsObras.size()) {
            throw new ResourceNotFoundException("Uma ou mais obras não foram encontradas.");
        }
        return new HashSet<>(obras);
    }
}
