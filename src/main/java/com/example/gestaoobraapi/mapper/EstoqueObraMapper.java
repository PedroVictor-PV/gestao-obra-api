package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.EstoqueObraRequest;
import com.example.gestaoobraapi.dto.EstoqueObraResponse;
import com.example.gestaoobraapi.model.EstoqueObra;
import com.example.gestaoobraapi.model.Material;
import com.example.gestaoobraapi.model.Obra;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EstoqueObraMapper {

    @Mapping(target = "idObra", source = "obra.id")
    @Mapping(target = "nomeObra", source = "obra.nome")
    @Mapping(target = "idMaterial", source = "material.id")
    @Mapping(target = "nomeMaterial", source = "material.nome")
    @Mapping(target = "idFornecedor", source = "fornecedor.id")
    @Mapping(target = "nomeFornecedor", source = "fornecedor.nome")
    EstoqueObraResponse toResponse(EstoqueObra estoqueObra);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "obra", source = "idObra")
    @Mapping(target = "material", source = "idMaterial")
    @Mapping(target = "fornecedor", ignore = true)
    EstoqueObra toModel(EstoqueObraRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "obra", source = "idObra")
    @Mapping(target = "material", source = "idMaterial")
    @Mapping(target = "fornecedor", ignore = true)
    void updateFromRequest(EstoqueObraRequest request, @MappingTarget EstoqueObra estoqueObra);

    default Obra mapObra(Long id) {
        if (id == null) {
            return null;
        }
        Obra obra = new Obra();
        obra.setId(id);
        return obra;
    }

    default Material mapMaterial(Long id) {
        if (id == null) {
            return null;
        }
        Material material = new Material();
        material.setId(id);
        return material;
    }
}
