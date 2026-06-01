package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.PermissaoRequest;
import com.example.gestaoobraapi.dto.PermissaoResponse;
import com.example.gestaoobraapi.model.Permissao;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PermissaoMapper {

    PermissaoResponse toResponse(Permissao permissao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    Permissao toModel(PermissaoRequest request);
}
