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

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface MaterialMapper {

    @Mapping(target = "idCategoria", source = "categoria.id")
    @Mapping(target = "nomeCategoria", source = "categoria.nome")
    @Mapping(target = "idFornecedor", source = "fornecedor.id")
    @Mapping(target = "nomeFornecedor", source = "fornecedor.nome")
    MaterialResponse toResponse(Material material);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "categoria", source = "idCategoria")
    @Mapping(target = "fornecedor", source = "idFornecedor")
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
}
