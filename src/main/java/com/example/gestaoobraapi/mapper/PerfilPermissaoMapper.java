package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.PerfilPermissaoRequest;
import com.example.gestaoobraapi.dto.PerfilPermissaoResponse;
import com.example.gestaoobraapi.model.Perfil;
import com.example.gestaoobraapi.model.PerfilPermissao;
import com.example.gestaoobraapi.model.Permissao;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PerfilPermissaoMapper {

    @Mapping(target = "idPerfil", source = "perfil.id")
    @Mapping(target = "nomePerfil", source = "perfil.nome")
    @Mapping(target = "idPermissao", source = "permissao.id")
    @Mapping(target = "nomePermissao", source = "permissao.nome")
    PerfilPermissaoResponse toResponse(PerfilPermissao perfilPermissao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "perfil", source = "idPerfil")
    @Mapping(target = "permissao", source = "idPermissao")
    PerfilPermissao toModel(PerfilPermissaoRequest request);

    default Perfil mapPerfil(Long id) {
        if (id == null) {
            return null;
        }
        Perfil perfil = new Perfil();
        perfil.setId(id);
        return perfil;
    }

    default Permissao mapPermissao(Long id) {
        if (id == null) {
            return null;
        }
        Permissao permissao = new Permissao();
        permissao.setId(id);
        return permissao;
    }
}
