package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.StatusServicoRequest;
import com.example.gestaoobraapi.dto.StatusServicoResponse;
import com.example.gestaoobraapi.model.StatusServico;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface StatusServicoMapper {
    StatusServicoResponse toResponse(StatusServico entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    StatusServico toModel(StatusServicoRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    void updateFromRequest(StatusServicoRequest request, @MappingTarget StatusServico entity);
}
