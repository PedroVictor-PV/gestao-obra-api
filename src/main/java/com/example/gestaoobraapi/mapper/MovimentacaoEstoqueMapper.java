package com.example.gestaoobraapi.mapper;

import com.example.gestaoobraapi.dto.MovimentacaoEstoqueResponse;
import com.example.gestaoobraapi.model.MovimentacaoEstoque;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface MovimentacaoEstoqueMapper {

    @Mapping(target = "idObra", source = "obra.id")
    @Mapping(target = "nomeObra", source = "obra.nome")
    @Mapping(target = "idMaterial", source = "material.id")
    @Mapping(target = "nomeMaterial", source = "material.nome")
    @Mapping(target = "idFornecedor", source = "fornecedor.id")
    @Mapping(target = "nomeFornecedor", source = "fornecedor.nome")
    MovimentacaoEstoqueResponse toResponse(MovimentacaoEstoque movimentacaoEstoque);
}
