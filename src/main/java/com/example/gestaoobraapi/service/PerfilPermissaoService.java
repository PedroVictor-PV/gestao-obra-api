package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.model.PerfilPermissao;
import com.example.gestaoobraapi.repository.PerfilPermissaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerfilPermissaoService {

    private final PerfilPermissaoRepository perfilPermissaoRepository;

    public PerfilPermissaoService(PerfilPermissaoRepository perfilPermissaoRepository) {
        this.perfilPermissaoRepository = perfilPermissaoRepository;
    }

    @Transactional(readOnly = true)
    public List<PerfilPermissao> listarTodas() {
        return perfilPermissaoRepository.findAll();
    }
}
