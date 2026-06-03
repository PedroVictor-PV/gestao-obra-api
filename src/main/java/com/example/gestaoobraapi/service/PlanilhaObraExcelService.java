package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PlanilhaObraExcelService {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] gerar(
            Obra obra,
            List<EstoqueObra> estoque,
            List<MovimentacaoEstoque> movimentacoes,
            List<Servico> servicos,
            List<ServicoMaterial> servicoMateriais) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = criarEstiloCabecalho(workbook);

            criarAbaResumo(workbook, headerStyle, obra);
            criarAbaEstoque(workbook, headerStyle, estoque);
            criarAbaMovimentacoes(workbook, headerStyle, movimentacoes);
            criarAbaServicos(workbook, headerStyle, servicos);
            criarAbaServicoMateriais(workbook, headerStyle, servicoMateriais);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar planilha Excel", e);
        }
    }

    private void criarAbaResumo(Workbook workbook, CellStyle headerStyle, Obra obra) {
        Sheet sheet = workbook.createSheet("Resumo");
        String[] headers = {
                "Código", "Nome", "Status", "Responsável", "Data início", "Data fim", "Descrição",
                "CEP", "Logradouro", "Número", "Complemento", "Bairro", "Cidade", "UF", "Ativo"
        };
        criarCabecalho(sheet, headerStyle, headers);

        Row row = sheet.createRow(1);
        int col = 0;
        row.createCell(col++).setCellValue(obra.getCodigo());
        row.createCell(col++).setCellValue(obra.getNome());
        row.createCell(col++).setCellValue(obra.getStatusObra() != null ? obra.getStatusObra().getNome() : "");
        // Mostrar nome do responsável em vez da chave
        String nomeResponsavel = "";
        if (obra.getResponsavel() != null) {
            nomeResponsavel = obra.getResponsavel().getNome() != null
                    ? obra.getResponsavel().getNome()
                    : obra.getResponsavel().getChave();
        }
        row.createCell(col++).setCellValue(nomeResponsavel);
        row.createCell(col++).setCellValue(formatarData(obra.getDataInicio()));
        row.createCell(col++).setCellValue(formatarData(obra.getDataFim()));
        row.createCell(col++).setCellValue(obra.getDescricao() != null ? obra.getDescricao() : "");
        // Campos de endereço
        row.createCell(col++).setCellValue(obra.getCep() != null ? obra.getCep() : "");
        row.createCell(col++).setCellValue(obra.getLogradouro() != null ? obra.getLogradouro() : "");
        row.createCell(col++).setCellValue(obra.getNumero() != null ? obra.getNumero() : "");
        row.createCell(col++).setCellValue(obra.getComplemento() != null ? obra.getComplemento() : "");
        row.createCell(col++).setCellValue(obra.getBairro() != null ? obra.getBairro() : "");
        row.createCell(col++).setCellValue(obra.getLocalidade() != null ? obra.getLocalidade() : "");
        row.createCell(col++).setCellValue(obra.getUf() != null ? obra.getUf() : "");
        row.createCell(col).setCellValue(Boolean.TRUE.equals(obra.getAtivo()) ? "Sim" : "Não");

        autoSize(sheet, headers.length);
    }

    private void criarAbaEstoque(Workbook workbook, CellStyle headerStyle, List<EstoqueObra> estoque) {
        Sheet sheet = workbook.createSheet("Estoque");
        String[] headers = {
                "Material", "Cód. material", "Fornecedor", "Qtd. atual", "Qtd. mínima", "Ativo"
        };
        criarCabecalho(sheet, headerStyle, headers);

        int rowIdx = 1;
        for (EstoqueObra item : estoque) {
            Row row = sheet.createRow(rowIdx++);
            int col = 0;
            row.createCell(col++).setCellValue(item.getMaterial().getNome());
            row.createCell(col++).setCellValue(item.getMaterial().getCodigo());
            row.createCell(col++).setCellValue(item.getFornecedor().getNome());
            setNumeric(row.createCell(col++), item.getQuantidadeAtual());
            setNumeric(row.createCell(col++), item.getQuantidadeMinima());
            row.createCell(col).setCellValue(Boolean.TRUE.equals(item.getAtivo()) ? "Sim" : "Não");
        }

        autoSize(sheet, headers.length);
    }

    private void criarAbaMovimentacoes(Workbook workbook, CellStyle headerStyle, List<MovimentacaoEstoque> movimentacoes) {
        Sheet sheet = workbook.createSheet("Movimentações");
        String[] headers = {
                "Data", "Tipo", "Material", "Fornecedor", "Quantidade", "Observação", "Registrado em"
        };
        criarCabecalho(sheet, headerStyle, headers);

        int rowIdx = 1;
        for (MovimentacaoEstoque mov : movimentacoes) {
            Row row = sheet.createRow(rowIdx++);
            int col = 0;
            row.createCell(col++).setCellValue(formatarData(mov.getDataMovimentacao()));
            row.createCell(col++).setCellValue(mov.getTipo() != null ? mov.getTipo().name() : "");
            row.createCell(col++).setCellValue(mov.getMaterial().getNome());
            row.createCell(col++).setCellValue(mov.getFornecedor() != null ? mov.getFornecedor().getNome() : "");
            setNumeric(row.createCell(col++), mov.getQuantidade());
            row.createCell(col++).setCellValue(mov.getObservacao() != null ? mov.getObservacao() : "");
            row.createCell(col).setCellValue(formatarDataHora(mov.getCriadoEm()));
        }

        autoSize(sheet, headers.length);
    }

    private void criarAbaServicos(Workbook workbook, CellStyle headerStyle, List<Servico> servicos) {
        Sheet sheet = workbook.createSheet("Serviços");
        String[] headers = {"Nome", "Status", "Descrição", "Observação", "Ativo"};
        criarCabecalho(sheet, headerStyle, headers);

        int rowIdx = 1;
        for (Servico servico : servicos) {
            Row row = sheet.createRow(rowIdx++);
            int col = 0;
            row.createCell(col++).setCellValue(servico.getNome());
            row.createCell(col++).setCellValue(servico.getStatusServico() != null ? servico.getStatusServico().getNome() : "");
            row.createCell(col++).setCellValue(servico.getDescricao() != null ? servico.getDescricao() : "");
            row.createCell(col++).setCellValue(servico.getObservacao() != null ? servico.getObservacao() : "");
            row.createCell(col).setCellValue(Boolean.TRUE.equals(servico.getAtivo()) ? "Sim" : "Não");
        }

        autoSize(sheet, headers.length);
    }

    private void criarAbaServicoMateriais(Workbook workbook, CellStyle headerStyle, List<ServicoMaterial> itens) {
        Sheet sheet = workbook.createSheet("Materiais nos serviços");
        String[] headers = {"Serviço", "Material", "Cód. material", "Quantidade", "Observação"};
        criarCabecalho(sheet, headerStyle, headers);

        int rowIdx = 1;
        for (ServicoMaterial item : itens) {
            Row row = sheet.createRow(rowIdx++);
            int col = 0;
            row.createCell(col++).setCellValue(item.getServico().getNome());
            row.createCell(col++).setCellValue(item.getMaterial().getNome());
            row.createCell(col++).setCellValue(item.getMaterial().getCodigo());
            setNumeric(row.createCell(col++), item.getQuantidade());
            row.createCell(col).setCellValue(item.getObservacao() != null ? item.getObservacao() : "");
        }

        autoSize(sheet, headers.length);
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void criarCabecalho(Sheet sheet, CellStyle headerStyle, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autoSize(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void setNumeric(Cell cell, BigDecimal value) {
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
    }

    private String formatarData(LocalDate data) {
        return data != null ? data.format(DATA) : "";
    }

    private String formatarDataHora(LocalDateTime dataHora) {
        return dataHora != null ? dataHora.format(DATA_HORA) : "";
    }
}
