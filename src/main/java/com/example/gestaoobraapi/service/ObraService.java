package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.ObraRequest;
import com.example.gestaoobraapi.exception.CodigoAlreadyExistsException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.ObraMapper;
import com.example.gestaoobraapi.model.Obra;
import com.example.gestaoobraapi.model.StatusObra;
import com.example.gestaoobraapi.model.Usuario;
import com.example.gestaoobraapi.model.UsuarioObra;
import com.example.gestaoobraapi.repository.ObraRepository;
import com.example.gestaoobraapi.repository.StatusObraRepository;
import com.example.gestaoobraapi.repository.UsuarioRepository;
import com.example.gestaoobraapi.repository.UsuarioObraRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ObraService {

    private final ObraRepository obraRepository;
    private final StatusObraRepository statusObraRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioObraRepository usuarioObraRepository;
    private final ObraMapper obraMapper;

    public ObraService(
            ObraRepository obraRepository,
            StatusObraRepository statusObraRepository,
            UsuarioRepository usuarioRepository,
            UsuarioObraRepository usuarioObraRepository,
            ObraMapper obraMapper) {
        this.obraRepository = obraRepository;
        this.statusObraRepository = statusObraRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioObraRepository = usuarioObraRepository;
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

        // Vincular automaticamente a todos os administradores
        List<Usuario> administradores = usuarioRepository.findAdministradores();
        for (Usuario admin : administradores) {
            UsuarioObra uo = new UsuarioObra();
            uo.setUsuario(admin);
            uo.setObra(obraSalva);
            uo.setAtivo(true);
            usuarioObraRepository.save(uo);
        }

        return obraRepository.findByIdWithStatusAndResponsavel(obraSalva.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + obraSalva.getId()));
    }

    @Transactional(readOnly = true)
    public List<Obra> listarTodas() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByChave(username).orElse(null);
        if (usuario == null) {
            return Collections.emptyList();
        }

        String perfilCodigo = usuarioRepository.findPerfilCodigoByUsuarioId(usuario.getId());
        if ("MESTRE_OBRAS".equals(perfilCodigo) || "OPERARIO".equals(perfilCodigo)) {
            java.util.Set<Long> obraIds = new java.util.HashSet<>();
            obraIds.addAll(usuarioObraRepository.findObraIdsByUsuarioId(usuario.getId()));
            obraIds.addAll(obraRepository.findObraIdsByResponsavelId(usuario.getId()));
            
            if (obraIds.isEmpty()) {
                return Collections.emptyList();
            }
            return obraRepository.findAllWithStatusAndResponsavelByIds(new java.util.ArrayList<>(obraIds));
        }

        return obraRepository.findAllWithStatusAndResponsavel();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarMestres() {
        return usuarioRepository.findMestresDeObras();
    }


    @Transactional(readOnly = true)
    public Obra buscarPorId(Long id) {
        Obra obra = obraRepository.findByIdWithStatusAndResponsavel(id)
                .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + id));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            usuarioRepository.findByChave(username).ifPresent(usuario -> {
                String perfilCodigo = usuarioRepository.findPerfilCodigoByUsuarioId(usuario.getId());
                if ("MESTRE_OBRAS".equals(perfilCodigo) || "OPERARIO".equals(perfilCodigo)) {
                    java.util.Set<Long> obraIds = new java.util.HashSet<>();
                    obraIds.addAll(usuarioObraRepository.findObraIdsByUsuarioId(usuario.getId()));
                    obraIds.addAll(obraRepository.findObraIdsByResponsavelId(usuario.getId()));

                    if (!obraIds.contains(id)) {
                        throw new ResourceNotFoundException("Obra não encontrada com id: " + id);
                    }
                }
            });
        }
        return obra;
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

    @Transactional(readOnly = true)
    public List<Usuario> listarOperadoresVinculados(Long idObra) {
        buscarPorId(idObra); // valida se a obra existe
        return usuarioObraRepository.findUsuariosByObraId(idObra);
    }

    @Transactional
    public void vincularOperador(Long idObra, Long idUsuario) {
        Obra obra = buscarPorId(idObra);
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + idUsuario));
        
        if (usuarioObraRepository.findByUsuarioIdAndObraId(idUsuario, idObra).isEmpty()) {
            UsuarioObra uo = new UsuarioObra();
            uo.setObra(obra);
            uo.setUsuario(usuario);
            uo.setAtivo(true);
            usuarioObraRepository.save(uo);
        }
    }

    @Transactional
    public void desvincularOperador(Long idObra, Long idUsuario) {
        buscarPorId(idObra); // valida acesso
        usuarioObraRepository.deleteByUsuarioIdAndObraId(idUsuario, idObra);
    }
}
