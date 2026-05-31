package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.model.Perfil;
import com.example.gestaoobraapi.repository.PerfilRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerfilService {

    private final PerfilRepository perfilRepository;

    public PerfilService(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    @Transactional(readOnly = true)
    public List<Perfil> listarTodos() {
        return perfilRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Perfil buscarPorId(Long id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado com id: " + id));
    }
}
