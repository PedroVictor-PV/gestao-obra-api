package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.FornecedorRequest;
import com.example.gestaoobraapi.dto.FornecedorResponse;
import com.example.gestaoobraapi.model.Fornecedor;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface FornecedorMapper {

    @Mapping(target = "idsObras", expression = "java(mapObrasToIds(fornecedor.getObras()))")
    FornecedorResponse toResponse(Fornecedor fornecedor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "obras", expression = "java(mapIdsToObras(request.getIdsObras()))")
    Fornecedor toModel(FornecedorRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "obras", ignore = true)
    void updateFromRequest(FornecedorRequest request, @MappingTarget Fornecedor fornecedor);

    default com.example.gestaoobraapi.model.Obra mapObra(Long id) {
        if (id == null) {
            return null;
        }
        com.example.gestaoobraapi.model.Obra obra = new com.example.gestaoobraapi.model.Obra();
        obra.setId(id);
        return obra;
    }

    default java.util.List<Long> mapObrasToIds(java.util.List<com.example.gestaoobraapi.model.Obra> obras) {
        if (obras == null) return null;
        return obras.stream().map(com.example.gestaoobraapi.model.Obra::getId).toList();
    }

    default java.util.List<com.example.gestaoobraapi.model.Obra> mapIdsToObras(java.util.List<Long> ids) {
        if (ids == null) return null;
        return ids.stream().map(this::mapObra).toList();
    }
}
