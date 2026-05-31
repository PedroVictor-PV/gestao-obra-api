package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.PermissaoResponse;
import com.example.gestaoobraapi.model.Permissao;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface PermissaoMapper {
    PermissaoResponse toResponse(Permissao permissao);
}
