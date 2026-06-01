package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.ServicoMaterialRequest;
import com.example.gestaoobraapi.dto.ServicoMaterialResponse;
import com.example.gestaoobraapi.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ServicoMaterialMapper {

    @Mapping(target = "idServico", source = "servico.id")
    @Mapping(target = "nomeServico", source = "servico.nome")
    @Mapping(target = "idObra", source = "obra.id")
    @Mapping(target = "nomeObra", source = "obra.nome")
    @Mapping(target = "idMaterial", source = "material.id")
    @Mapping(target = "nomeMaterial", source = "material.nome")
    ServicoMaterialResponse toResponse(ServicoMaterial entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "servico", source = "idServico")
    @Mapping(target = "obra", source = "idObra")
    @Mapping(target = "material", source = "idMaterial")
    ServicoMaterial toModel(ServicoMaterialRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "servico", source = "idServico")
    @Mapping(target = "obra", source = "idObra")
    @Mapping(target = "material", source = "idMaterial")
    void updateFromRequest(ServicoMaterialRequest request, @MappingTarget ServicoMaterial entity);

    default Servico mapServico(Long id) {
        if (id == null) return null;
        Servico s = new Servico();
        s.setId(id);
        return s;
    }

    default Obra mapObra(Long id) {
        if (id == null) return null;
        Obra o = new Obra();
        o.setId(id);
        return o;
    }

    default Material mapMaterial(Long id) {
        if (id == null) return null;
        Material m = new Material();
        m.setId(id);
        return m;
    }
}
