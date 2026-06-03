package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.ObraRequest;
import com.example.gestaoobraapi.dto.ObraResponse;
import com.example.gestaoobraapi.model.Obra;
import com.example.gestaoobraapi.model.StatusObra;
import com.example.gestaoobraapi.model.Usuario;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ObraMapper {

    @Mapping(target = "idStatusObra", source = "statusObra.id")
    @Mapping(target = "nomeStatusObra", source = "statusObra.nome")
    @Mapping(target = "idResponsavel", source = "responsavel.id")
    @Mapping(target = "chaveResponsavel", source = "responsavel.chave")
    @Mapping(target = "nomeResponsavel", source = "responsavel.nome")
    ObraResponse toResponse(Obra obra);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "statusObra", source = "idStatusObra")
    @Mapping(target = "responsavel", source = "idResponsavel")
    Obra toModel(ObraRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "statusObra", source = "idStatusObra")
    @Mapping(target = "responsavel", source = "idResponsavel")
    void updateFromRequest(ObraRequest request, @MappingTarget Obra obra);

    default StatusObra mapStatusObra(Long id) {
        if (id == null) {
            return null;
        }
        StatusObra statusObra = new StatusObra();
        statusObra.setId(id);
        return statusObra;
    }

    default Usuario mapResponsavel(Long id) {
        if (id == null) {
            return null;
        }
        Usuario responsavel = new Usuario();
        responsavel.setId(id);
        return responsavel;
    }
}
