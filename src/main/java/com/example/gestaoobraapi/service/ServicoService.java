package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.ServicoRequest;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.ServicoMapper;
import com.example.gestaoobraapi.model.*;
import com.example.gestaoobraapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoService {
    private final ServicoRepository servicoRepository;
    private final ObraRepository obraRepository;
    private final StatusServicoRepository statusServicoRepository;
    private final ServicoMapper mapper;

    public ServicoService(ServicoRepository servicoRepository, ObraRepository obraRepository,
                          StatusServicoRepository statusServicoRepository, ServicoMapper mapper) {
        this.servicoRepository = servicoRepository;
        this.obraRepository = obraRepository;
        this.statusServicoRepository = statusServicoRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Servico criar(Servico servico) {
        servico.setObra(buscarObra(servico.getObra().getId()));
        servico.setStatusServico(buscarStatus(servico.getStatusServico().getId()));
        Servico salvo = servicoRepository.save(servico);
        return servicoRepository.findByIdWithRelations(salvo.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com id: " + salvo.getId()));
    }

    @Transactional(readOnly = true)
    public List<Servico> listarTodos() {
        return servicoRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public Servico buscarPorId(Long id) {
        return servicoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com id: " + id));
    }

    @Transactional
    public Servico atualizar(Long id, ServicoRequest request) {
        Servico servico = buscarPorId(id);
        mapper.updateFromRequest(request, servico);
        servico.setObra(buscarObra(request.getIdObra()));
        servico.setStatusServico(buscarStatus(request.getIdStatusServico()));
        return servicoRepository.save(servico);
    }

    @Transactional
    public void excluir(Long id) {
        servicoRepository.delete(buscarPorId(id));
    }

    private Obra buscarObra(Long id) {
        return obraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + id));
    }

    private StatusServico buscarStatus(Long id) {
        return statusServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status de serviço não encontrado com id: " + id));
    }
}
