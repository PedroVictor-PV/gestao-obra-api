package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.MaterialRequest;
import com.example.gestaoobraapi.dto.MaterialResponse;
import com.example.gestaoobraapi.model.CategoriaMaterial;
import com.example.gestaoobraapi.model.Fornecedor;
import com.example.gestaoobraapi.model.Material;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface MaterialMapper {

    @Mapping(target = "idCategoria", source = "categoria.id")
    @Mapping(target = "nomeCategoria", source = "categoria.nome")
    @Mapping(target = "idFornecedor", source = "fornecedor.id")
    @Mapping(target = "nomeFornecedor", source = "fornecedor.nome")
    @Mapping(target = "idsObras", expression = "java(mapObrasToIds(material.getObras()))")
    MaterialResponse toResponse(Material material);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "categoria", source = "idCategoria")
    @Mapping(target = "fornecedor", source = "idFornecedor")
    @Mapping(target = "obras", ignore = true)
    Material toModel(MaterialRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "categoria", source = "idCategoria")
    @Mapping(target = "fornecedor", source = "idFornecedor")
    @Mapping(target = "obras", ignore = true)
    void updateFromRequest(MaterialRequest request, @MappingTarget Material material);

    default CategoriaMaterial mapCategoria(Long id) {
        if (id == null) {
            return null;
        }
        CategoriaMaterial categoria = new CategoriaMaterial();
        categoria.setId(id);
        return categoria;
    }

    default Fornecedor mapFornecedor(Long id) {
        if (id == null) {
            return null;
        }
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(id);
        return fornecedor;
    }

    default List<Long> mapObrasToIds(Set<com.example.gestaoobraapi.model.Obra> obras) {
        if (obras == null) {
            return new ArrayList<>();
        }
        return obras.stream()
                .map(com.example.gestaoobraapi.model.Obra::getId)
                .collect(Collectors.toList());
    }
}
