package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.PerfilRequest;
import com.example.gestaoobraapi.dto.PerfilResponse;
import com.example.gestaoobraapi.model.Perfil;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PerfilMapper {

    PerfilResponse toResponse(Perfil perfil);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    Perfil toModel(PerfilRequest request);
}
