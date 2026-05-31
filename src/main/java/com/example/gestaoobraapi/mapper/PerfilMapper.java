package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.PerfilResponse;
import com.example.gestaoobraapi.model.Perfil;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PerfilMapper {
    PerfilResponse toResponse(Perfil perfil);
}
