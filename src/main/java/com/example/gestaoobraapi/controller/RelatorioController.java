package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.dto.MovimentacaoEstoqueResponse;
import com.example.gestaoobraapi.mapper.MovimentacaoEstoqueMapper;
import com.example.gestaoobraapi.model.MovimentacaoEstoque;
import com.example.gestaoobraapi.model.Obra;
import com.example.gestaoobraapi.security.PermissaoCodigo;
import com.example.gestaoobraapi.exception.BusinessException;
import com.example.gestaoobraapi.service.MovimentacaoEstoqueCsvService;
import com.example.gestaoobraapi.service.MovimentacaoEstoqueService;
import com.example.gestaoobraapi.service.ObraService;
import com.example.gestaoobraapi.service.RelatorioObraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatorios", description = "Exportação de planilhas e consultas analíticas")
@SecurityRequirement(name = "bearerAuth")
public class RelatorioController {

    private static final String MEDIA_TYPE_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String MEDIA_TYPE_CSV = "text/csv";

    private final RelatorioObraService relatorioObraService;
    private final ObraService obraService;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final MovimentacaoEstoqueMapper movimentacaoEstoqueMapper;
    private final MovimentacaoEstoqueCsvService movimentacaoEstoqueCsvService;

    public RelatorioController(
            RelatorioObraService relatorioObraService,
            ObraService obraService,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            MovimentacaoEstoqueMapper movimentacaoEstoqueMapper,
            MovimentacaoEstoqueCsvService movimentacaoEstoqueCsvService) {
        this.relatorioObraService = relatorioObraService;
        this.obraService = obraService;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.movimentacaoEstoqueMapper = movimentacaoEstoqueMapper;
        this.movimentacaoEstoqueCsvService = movimentacaoEstoqueCsvService;
    }

    @GetMapping(value = "/obra/{idObra}", produces = MEDIA_TYPE_XLSX)
    @PreAuthorize("hasAnyAuthority('"
            + PermissaoCodigo.RELATORIO_OBRA_EXPORTAR + "', '"
            + PermissaoCodigo.OBRA_LISTAR + "')")
    @Operation(
            summary = "Exportar planilha consolidada da obra",
            description = """
                    Gera arquivo Excel (.xlsx) com abas: Resumo, Estoque, Movimentações, Serviços e Materiais nos serviços.

                    Exemplo: GET /relatorios/obra/1?formato=xlsx
                    (obra seed OBR-001 = id 1 — Residencial Jardim das Palmeiras)
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Arquivo Excel (.xlsx) para download",
                            content = @Content(
                                    mediaType = MEDIA_TYPE_XLSX,
                                    schema = @Schema(type = "string", format = "binary"),
                                    examples = @ExampleObject(
                                            name = "planilha-obra-xlsx",
                                            description = "Arquivo binário .xlsx (5 abas)",
                                            value = "Arquivo Excel — use Download file no Swagger ou curl -o relatorio.xlsx"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Sem permissão RELATORIO_OBRA_EXPORTAR ou OBRA_LISTAR"),
                    @ApiResponse(responseCode = "404", description = "Obra não encontrada")
            }
    )
    public ResponseEntity<byte[]> exportarPlanilhaObra(
            @Parameter(
                    name = "idObra",
                    in = ParameterIn.PATH,
                    description = "ID da obra (seed OBR-001 = 1)",
                    required = true,
                    example = "1"
            )
            @PathVariable("idObra") Long idObra,
            @Parameter(
                    name = "formato",
                    in = ParameterIn.QUERY,
                    description = "Formato do arquivo (apenas xlsx)",
                    example = "xlsx",
                    schema = @Schema(type = "string", allowableValues = {"xlsx"}, defaultValue = "xlsx")
            )
            @RequestParam(value = "formato", defaultValue = "xlsx") String formato) {
        Obra obra = obraService.buscarPorId(idObra);
        byte[] planilha = relatorioObraService.exportarPlanilhaObra(idObra, formato);
        String nomeArquivo = relatorioObraService.nomeArquivoPlanilha(obra);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType(MEDIA_TYPE_XLSX))
                .body(planilha);
    }

    @GetMapping(value = "/movimentacao/estoque", produces = {MediaType.APPLICATION_JSON_VALUE, MEDIA_TYPE_CSV})
    @PreAuthorize("hasAnyAuthority('"
            + PermissaoCodigo.RELATORIO_MOVIMENTACAO_LISTAR + "', '"
            + PermissaoCodigo.MOVIMENTACAO_ESTOQUE_LISTAR + "')")
    @Operation(
            summary = "Listar ou exportar movimentações de estoque",
            description = """
                    Filtros opcionais: idObra, dataInicio, dataFim (yyyy-MM-dd).
                    - formato=json → retorna JSON
                    - formato=csv → baixa arquivo CSV

                    Exemplo: GET /relatorios/movimentacao/estoque?formato=csv&idObra=1&dataInicio=2026-01-01&dataFim=2026-06-30
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista JSON ou arquivo CSV",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = MovimentacaoEstoqueResponse.class),
                                            examples = @ExampleObject(
                                                    name = "movimentacoes-json",
                                                    value = """
                                                            [
                                                              {
                                                                "id": 1,
                                                                "idObra": 1,
                                                                "nomeObra": "Residencial Jardim das Palmeiras",
                                                                "idMaterial": 1,
                                                                "nomeMaterial": "Tubo PVC 50mm",
                                                                "idFornecedor": 1,
                                                                "nomeFornecedor": "Construmax Materiais",
                                                                "tipo": "ENTRADA",
                                                                "quantidade": 100.0,
                                                                "dataMovimentacao": "2026-03-15",
                                                                "observacao": "Entrada inicial",
                                                                "ativo": true
                                                              }
                                                            ]
                                                            """
                                            )
                                    ),
                                    @Content(
                                            mediaType = MEDIA_TYPE_CSV,
                                            examples = @ExampleObject(
                                                    name = "movimentacoes-csv",
                                                    description = "Arquivo CSV (separador ;)",
                                                    value = "nomeObra;nomeMaterial;nomeFornecedor;tipo;quantidade;dataMovimentacao;observacao"
                                            )
                                    )
                            }
                    )
            }
    )
    public ResponseEntity<?> listarMovimentacoesEstoque(
            @Parameter(
                    name = "formato",
                    in = ParameterIn.QUERY,
                    description = "json (padrão) ou csv",
                    example = "csv",
                    schema = @Schema(type = "string", allowableValues = {"json", "csv"}, defaultValue = "json")
            )
            @RequestParam(value = "formato", defaultValue = "json") String formato,
            @Parameter(
                    name = "idObra",
                    in = ParameterIn.QUERY,
                    description = "Filtrar por obra (ex.: OBR-001 = id 1)",
                    example = "1"
            )
            @RequestParam(required = false) Long idObra,
            @Parameter(
                    name = "dataInicio",
                    in = ParameterIn.QUERY,
                    description = "Data inicial (inclusive), formato yyyy-MM-dd",
                    example = "2026-01-01"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(
                    name = "dataFim",
                    in = ParameterIn.QUERY,
                    description = "Data final (inclusive), formato yyyy-MM-dd",
                    example = "2026-06-30"
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<MovimentacaoEstoqueResponse> dados = movimentacaoEstoqueService
                .listarComFiltros(idObra, dataInicio, dataFim)
                .stream()
                .map(movimentacaoEstoqueMapper::toResponse)
                .toList();

        String formatoNormalizado = formato.trim().toLowerCase(Locale.ROOT);
        if ("csv".equals(formatoNormalizado)) {
            byte[] arquivo = movimentacaoEstoqueCsvService.gerar(dados);
            String nomeArquivo = movimentacaoEstoqueCsvService.nomeArquivo(idObra);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                    .contentType(MediaType.parseMediaType(MEDIA_TYPE_CSV))
                    .body(arquivo);
        }
        if ("json".equals(formatoNormalizado)) {
            return ResponseEntity.ok(dados);
        }
        throw new BusinessException("Formato não suportado: " + formato + ". Use formato=json ou formato=csv.");
    }
}
