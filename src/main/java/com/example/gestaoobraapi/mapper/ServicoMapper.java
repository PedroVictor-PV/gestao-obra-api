package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.ServicoRequest;
import com.example.gestaoobraapi.dto.ServicoResponse;
import com.example.gestaoobraapi.model.Obra;
import com.example.gestaoobraapi.model.Servico;
import com.example.gestaoobraapi.model.StatusServico;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ServicoMapper {

    @Mapping(target = "idObra", source = "obra.id")
    @Mapping(target = "nomeObra", source = "obra.nome")
    @Mapping(target = "idStatusServico", source = "statusServico.id")
    @Mapping(target = "nomeStatusServico", source = "statusServico.nome")
    ServicoResponse toResponse(Servico servico);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "obra", source = "idObra")
    @Mapping(target = "statusServico", source = "idStatusServico")
    Servico toModel(ServicoRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "alteradoPor", ignore = true)
    @Mapping(target = "alteradoEm", ignore = true)
    @Mapping(target = "obra", source = "idObra")
    @Mapping(target = "statusServico", source = "idStatusServico")
    void updateFromRequest(ServicoRequest request, @MappingTarget Servico servico);

    default Obra mapObra(Long id) {
        if (id == null) return null;
        Obra o = new Obra();
        o.setId(id);
        return o;
    }

    default StatusServico mapStatus(Long id) {
        if (id == null) return null;
        StatusServico s = new StatusServico();
        s.setId(id);
        return s;
    }
}
