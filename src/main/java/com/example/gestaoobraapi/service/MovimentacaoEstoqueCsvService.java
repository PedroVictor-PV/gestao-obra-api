package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.MovimentacaoEstoqueResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MovimentacaoEstoqueCsvService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final String CABECALHO =
            "nomeObra;nomeMaterial;nomeFornecedor;tipo;quantidade;dataMovimentacao;observacao";

    public byte[] gerar(List<MovimentacaoEstoqueResponse> movimentacoes) {
        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append(CABECALHO).append('\n');

        for (MovimentacaoEstoqueResponse item : movimentacoes) {
            csv.append(escapar(item.getNomeObra())).append(';');
            csv.append(escapar(item.getNomeMaterial())).append(';');
            csv.append(escapar(item.getNomeFornecedor())).append(';');
            csv.append(escapar(item.getTipo())).append(';');
            csv.append(escapar(item.getQuantidade())).append(';');
            csv.append(escapar(item.getDataMovimentacao() != null ? item.getDataMovimentacao().format(DATA) : null))
                    .append(';');
            csv.append(escapar(item.getObservacao()));
            csv.append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public String nomeArquivo(Long idObra) {
        if (idObra != null) {
            return "movimentacoes-estoque-obra-" + idObra + ".csv";
        }
        return "movimentacoes-estoque.csv";
    }

    private String escapar(Object valor) {
        if (valor == null) {
            return "";
        }
        String texto = String.valueOf(valor);
        if (texto.contains(";") || texto.contains("\"") || texto.contains("\n") || texto.contains("\r")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}
