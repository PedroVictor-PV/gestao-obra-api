package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.UserRequest;
import com.example.gestaoobraapi.dto.UsuarioResponse;
import com.example.gestaoobraapi.model.Usuario;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UsuarioMapper {
    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    Usuario toModel(UserRequest request);
}
