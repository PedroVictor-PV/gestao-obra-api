package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.ObraRequest;
import com.example.gestaoobraapi.exception.CodigoAlreadyExistsException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.ObraMapper;
import com.example.gestaoobraapi.model.Obra;
import com.example.gestaoobraapi.model.StatusObra;
import com.example.gestaoobraapi.model.Usuario;
import com.example.gestaoobraapi.repository.ObraRepository;
import com.example.gestaoobraapi.repository.StatusObraRepository;
import com.example.gestaoobraapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ObraService {

    private final ObraRepository obraRepository;
    private final StatusObraRepository statusObraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObraMapper obraMapper;

    public ObraService(
            ObraRepository obraRepository,
            StatusObraRepository statusObraRepository,
            UsuarioRepository usuarioRepository,
            ObraMapper obraMapper) {
        this.obraRepository = obraRepository;
        this.statusObraRepository = statusObraRepository;
        this.usuarioRepository = usuarioRepository;
        this.obraMapper = obraMapper;
    }

    @Transactional
    public Obra criar(Obra obra) {
        if (obraRepository.findByCodigo(obra.getCodigo()).isPresent()) {
            throw new CodigoAlreadyExistsException("O código '" + obra.getCodigo() + "' já está em uso.");
        }
        obra.setStatusObra(buscarStatusObra(obra.getStatusObra().getId()));
        obra.setResponsavel(resolverResponsavel(obra.getResponsavel()));
        Obra obraSalva = obraRepository.save(obra);
        return obraRepository.findByIdWithStatusAndResponsavel(obraSalva.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + obraSalva.getId()));
    }

    @Transactional(readOnly = true)
    public List<Obra> listarTodas() {
        return obraRepository.findAllWithStatusAndResponsavel();
    }

    @Transactional(readOnly = true)
    public Obra buscarPorId(Long id) {
        return obraRepository.findByIdWithStatusAndResponsavel(id)
                .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + id));
    }

    @Transactional
    public Obra atualizar(Long id, ObraRequest request) {
        Obra obra = buscarPorId(id);
        if (obraRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new CodigoAlreadyExistsException("O código '" + request.getCodigo() + "' já está em uso.");
        }
        obraMapper.updateFromRequest(request, obra);
        obra.setStatusObra(buscarStatusObra(request.getIdStatusObra()));
        obra.setResponsavel(resolverResponsavelPorId(request.getIdResponsavel()));
        return obraRepository.save(obra);
    }

    @Transactional
    public void excluir(Long id) {
        Obra obra = buscarPorId(id);
        obraRepository.delete(obra);
    }

    private StatusObra buscarStatusObra(Long id) {
        return statusObraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status de obra não encontrado com id: " + id));
    }

    private Usuario resolverResponsavel(Usuario responsavel) {
        if (responsavel == null || responsavel.getId() == null) {
            return null;
        }
        return resolverResponsavelPorId(responsavel.getId());
    }

    private Usuario resolverResponsavelPorId(Long id) {
        if (id == null) {
            return null;
        }
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com id: " + id));
    }
}
