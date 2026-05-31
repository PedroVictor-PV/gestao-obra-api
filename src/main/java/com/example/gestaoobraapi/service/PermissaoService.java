package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.model.Permissao;
import com.example.gestaoobraapi.repository.PermissaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissaoService {

    private final PermissaoRepository permissaoRepository;

    public PermissaoService(PermissaoRepository permissaoRepository) {
        this.permissaoRepository = permissaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Permissao> listarTodas() {
        return permissaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Permissao buscarPorId(Long id) {
        return permissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permissão não encontrada com id: " + id));
    }
}
