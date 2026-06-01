package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.exception.BusinessException;
import com.example.gestaoobraapi.model.*;
import com.example.gestaoobraapi.repository.EstoqueObraRepository;
import com.example.gestaoobraapi.repository.MovimentacaoEstoqueRepository;
import com.example.gestaoobraapi.repository.ServicoMaterialRepository;
import com.example.gestaoobraapi.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class RelatorioObraService {

    private final ObraService obraService;
    private final EstoqueObraRepository estoqueObraRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final ServicoRepository servicoRepository;
    private final ServicoMaterialRepository servicoMaterialRepository;
    private final PlanilhaObraExcelService planilhaObraExcelService;

    public RelatorioObraService(
            ObraService obraService,
            EstoqueObraRepository estoqueObraRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            ServicoRepository servicoRepository,
            ServicoMaterialRepository servicoMaterialRepository,
            PlanilhaObraExcelService planilhaObraExcelService) {
        this.obraService = obraService;
        this.estoqueObraRepository = estoqueObraRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.servicoRepository = servicoRepository;
        this.servicoMaterialRepository = servicoMaterialRepository;
        this.planilhaObraExcelService = planilhaObraExcelService;
    }

    @Transactional(readOnly = true)
    public byte[] exportarPlanilhaObra(Long obraId, String formato) {
        String formatoNormalizado = formato != null ? formato.trim().toLowerCase(Locale.ROOT) : "xlsx";
        if (!"xlsx".equals(formatoNormalizado)) {
            throw new BusinessException("Formato não suportado: " + formato + ". Use formato=xlsx.");
        }

        Obra obra = obraService.buscarPorId(obraId);
        List<EstoqueObra> estoque = estoqueObraRepository.findAllByObraIdWithRelations(obraId);
        List<MovimentacaoEstoque> movimentacoes = movimentacaoEstoqueRepository.findAllByObraIdWithRelations(obraId);
        List<Servico> servicos = servicoRepository.findAllByObraIdWithRelations(obraId);
        List<ServicoMaterial> servicoMateriais = servicoMaterialRepository.findAllByObraIdWithRelations(obraId);

        return planilhaObraExcelService.gerar(obra, estoque, movimentacoes, servicos, servicoMateriais);
    }

    public String nomeArquivoPlanilha(Obra obra) {
        String codigo = obra.getCodigo() != null ? obra.getCodigo().replaceAll("[^a-zA-Z0-9_-]", "_") : "obra";
        return "relatorio-obra-" + codigo + ".xlsx";
    }
}
