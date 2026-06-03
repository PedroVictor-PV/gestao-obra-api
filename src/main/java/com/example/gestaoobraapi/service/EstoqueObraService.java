package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.dto.EstoqueObraRequest;
import com.example.gestaoobraapi.dto.VozEstoqueRequest;
import com.example.gestaoobraapi.exception.BusinessException;
import com.example.gestaoobraapi.exception.ResourceNotFoundException;
import com.example.gestaoobraapi.mapper.EstoqueObraMapper;
import com.example.gestaoobraapi.model.EstoqueObra;
import com.example.gestaoobraapi.model.Material;
import com.example.gestaoobraapi.model.Obra;
import com.example.gestaoobraapi.model.TipoMovimentacao;
import com.example.gestaoobraapi.repository.EstoqueObraRepository;
import com.example.gestaoobraapi.repository.MaterialRepository;
import com.example.gestaoobraapi.repository.ObraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueObraService {

    private final EstoqueObraRepository estoqueObraRepository;
    private final ObraRepository obraRepository;
    private final MaterialRepository materialRepository;
    private final EstoqueObraMapper estoqueObraMapper;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public EstoqueObraService(
            EstoqueObraRepository estoqueObraRepository,
            ObraRepository obraRepository,
            MaterialRepository materialRepository,
            EstoqueObraMapper estoqueObraMapper,
            MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.estoqueObraRepository = estoqueObraRepository;
        this.obraRepository = obraRepository;
        this.materialRepository = materialRepository;
        this.estoqueObraMapper = estoqueObraMapper;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @Transactional
    public EstoqueObra criar(EstoqueObra estoqueObra, String observacao) {
        Long obraId = estoqueObra.getObra().getId();
        Long materialId = estoqueObra.getMaterial().getId();
        BigDecimal quantidadeEntrada = estoqueObra.getQuantidadeAtual();

        Obra obra = buscarObra(obraId);
        Material material = buscarMaterialComFornecedor(materialId);
        Long fornecedorId = material.getFornecedor().getId();

        Optional<EstoqueObra> estoqueExistente = estoqueObraRepository
                .findByObraIdAndMaterialIdAndFornecedorIdWithRelations(obraId, materialId, fornecedorId);

        if (estoqueExistente.isPresent()) {
            return consolidarEntrada(
                    estoqueExistente.get(), quantidadeEntrada, estoqueObra.getQuantidadeMinima(), observacao);
        }

        estoqueObra.setObra(obra);
        estoqueObra.setMaterial(material);
        estoqueObra.setFornecedor(material.getFornecedor());
        EstoqueObra estoqueSalvo = estoqueObraRepository.save(estoqueObra);
        EstoqueObra estoqueCompleto = estoqueObraRepository.findByIdWithObraAndMaterial(estoqueSalvo.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado com id: " + estoqueSalvo.getId()));
        movimentacaoEstoqueService.registrarEntradaEstoque(estoqueCompleto, quantidadeEntrada, observacao);
        return estoqueCompleto;
    }

    @Transactional(readOnly = true)
    public List<EstoqueObra> listarTodos() {
        return estoqueObraRepository.findAllWithObraAndMaterial();
    }

    @Transactional(readOnly = true)
    public EstoqueObra buscarPorId(Long id) {
        return estoqueObraRepository.findByIdWithObraAndMaterial(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado com id: " + id));
    }

    @Transactional
    public EstoqueObra atualizar(Long id, EstoqueObraRequest request) {
        EstoqueObra estoqueObra = buscarPorId(id);
        BigDecimal quantidadeAnterior = estoqueObra.getQuantidadeAtual();
        Material material = buscarMaterialComFornecedor(request.getIdMaterial());
        Long fornecedorId = material.getFornecedor().getId();

        if (estoqueObraRepository.existsByObraIdAndMaterialIdAndFornecedorIdAndIdNot(
                request.getIdObra(), request.getIdMaterial(), fornecedorId, id)) {
            throw new BusinessException(
                    "Já existe estoque cadastrado para esta obra, material e fornecedor.");
        }
        estoqueObraMapper.updateFromRequest(request, estoqueObra);
        estoqueObra.setObra(buscarObra(request.getIdObra()));
        estoqueObra.setMaterial(material);
        estoqueObra.setFornecedor(material.getFornecedor());
        EstoqueObra estoqueAtualizado = estoqueObraRepository.save(estoqueObra);
        movimentacaoEstoqueService.registrarAjusteEstoque(
                estoqueAtualizado,
                quantidadeAnterior,
                estoqueAtualizado.getQuantidadeAtual(),
                request.getObservacao());
        return estoqueAtualizado;
    }

    @Transactional
    public void excluir(Long id) {
        EstoqueObra estoqueObra = buscarPorId(id);
        movimentacaoEstoqueService.registrarSaidaEstoque(estoqueObra, estoqueObra.getQuantidadeAtual(), null);
        estoqueObraRepository.delete(estoqueObra);
    }

    private EstoqueObra consolidarEntrada(
            EstoqueObra estoqueExistente,
            BigDecimal quantidadeEntrada,
            BigDecimal quantidadeMinima,
            String observacao) {
        BigDecimal quantidadeAtualizada = estoqueExistente.getQuantidadeAtual().add(quantidadeEntrada);
        estoqueExistente.setQuantidadeAtual(quantidadeAtualizada);
        if (quantidadeMinima != null) {
            estoqueExistente.setQuantidadeMinima(quantidadeMinima);
        }

        EstoqueObra estoqueAtualizado = estoqueObraRepository.save(estoqueExistente);
        movimentacaoEstoqueService.registrarEntradaEstoque(estoqueAtualizado, quantidadeEntrada, observacao);
        return estoqueAtualizado;
    }

    private Obra buscarObra(Long id) {
        return obraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Obra não encontrada com id: " + id));
    }

    private Material buscarMaterialComFornecedor(Long id) {
        return materialRepository.findByIdWithCategoriaAndFornecedor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material não encontrado com id: " + id));
    }

    /**
     * Processa um comando de voz já transcrito e extraído pelo microsserviço Python.
     * <p>
     * Fluxo:
     * 1. Busca o material pelo nome no banco (LIKE, case-insensitive)
     * 2. Detecta a intenção: ENTRADA ou SAÍDA usando {@link #detectarSubtracao}
     * 3. Cria, consolida ou subtrai do EstoqueObra e registra a MovimentacaoEstoque
     */
    @Transactional
    public EstoqueObra processarComandoVoz(VozEstoqueRequest request) {
        Obra obra = buscarObra(request.getIdObra());

        // 1. Busca todos os materiais da obra e faz match tolerante em memória
        String nomeBuscado = request.getMaterial() == null ? "" : request.getMaterial().trim();
        String nomeBuscadoNormalizado = removerAcentos(nomeBuscado.toLowerCase()).replaceAll("s\\b", "");
        
        List<Material> todosMateriaisObra = materialRepository.findByObraIdWithRelations(obra.getId());
        List<Material> materiais = todosMateriaisObra.stream().filter(m -> {
            String dbMaterial = removerAcentos(m.getNome().toLowerCase());
            String dbSemS = dbMaterial.replaceAll("s\\b", "");
            return dbMaterial.contains(nomeBuscadoNormalizado) 
                || nomeBuscadoNormalizado.contains(dbMaterial)
                || dbSemS.contains(nomeBuscadoNormalizado)
                || nomeBuscadoNormalizado.contains(dbSemS);
        }).toList();

        // Fallback: se não encontrou, tenta quebrar em palavras
        if (materiais.isEmpty() && !nomeBuscado.isBlank() && !nomeBuscado.toLowerCase().contains("identificado")) {
            String[] palavras = nomeBuscadoNormalizado.split("\\s+");
            for (String palavra : palavras) {
                if (palavra.length() > 2) { // Ignora "de", "da", "do"
                    materiais = todosMateriaisObra.stream().filter(m -> {
                        String dbMaterial = removerAcentos(m.getNome().toLowerCase()).replaceAll("s\\b", "");
                        return dbMaterial.contains(palavra) || palavra.contains(dbMaterial);
                    }).toList();
                    if (!materiais.isEmpty()) {
                        break;
                    }
                }
            }
        }

        String fornecedorBuscado = request.getFornecedor() == null ? "" : request.getFornecedor().trim();
        boolean temFornecedorNaVoz = !fornecedorBuscado.isBlank() && !fornecedorBuscado.toLowerCase().contains("identificado");

        if (temFornecedorNaVoz && !materiais.isEmpty()) {
            String fornecedorBuscadoNormalizado = removerAcentos(fornecedorBuscado.toLowerCase());
            List<Material> filtrados = materiais.stream()
                .filter(m -> {
                    if (m.getFornecedor() == null) return false;
                    String dbFornecedor = removerAcentos(m.getFornecedor().getNome().toLowerCase());
                    String buscaSemS = fornecedorBuscadoNormalizado.replaceAll("s\\b", "");
                    String dbSemS = dbFornecedor.replaceAll("s\\b", "");
                    return dbFornecedor.contains(fornecedorBuscadoNormalizado) 
                        || fornecedorBuscadoNormalizado.contains(dbFornecedor)
                        || dbSemS.contains(buscaSemS)
                        || buscaSemS.contains(dbSemS);
                })
                .toList();
            
            if (filtrados.isEmpty()) {
                if (materiais.size() == 1) {
                    // Fallback: se só existe 1 material com esse nome, ignora o erro de fornecedor (pode ser erro de transcrição da voz)
                    filtrados = materiais;
                } else {
                    throw new BusinessException(
                            "O produto '" + nomeBuscado + "' não possui cadastro vinculado ao fornecedor '" + fornecedorBuscado + "'. " +
                            "Não é permitido o recebimento de produtos sem cadastro prévio no respectivo fornecedor.");
                }
            }
            materiais = filtrados;
        }

        if (materiais.isEmpty()) {
            throw new BusinessException(
                    "Nenhum material encontrado no sistema para o termo: '" + nomeBuscado + "'. " +
                    "O administrador precisa cadastrar este produto no sistema antes de realizar o recebimento na obra.");
        }

        Material material = materiais.get(0);
        Long fornecedorId = material.getFornecedor().getId();

        BigDecimal quantidade = request.getQuantidade() != null
                ? request.getQuantidade()
                : BigDecimal.ONE;

        // 2. Detecta intenção de saída com análise robusta do campo acao
        boolean isSubtrair = detectarSubtracao(request.getAcao());

        // 3. Verifica se já existe estoque para essa combinação obra+material+fornecedor
        Optional<EstoqueObra> estoqueExistente = estoqueObraRepository
                .findByObraIdAndMaterialIdAndFornecedorIdWithRelations(
                        obra.getId(), material.getId(), fornecedorId);

        if (estoqueExistente.isPresent()) {
            EstoqueObra estoque = estoqueExistente.get();
            BigDecimal quantidadeAtual = estoque.getQuantidadeAtual();

            if (isSubtrair) {
                // SAÍDA: valida saldo e subtrai
                if (quantidadeAtual.compareTo(quantidade) < 0) {
                    throw new BusinessException(
                            "Estoque insuficiente para '" + material.getNome() + "'. " +
                            "Disponível: " + quantidadeAtual + " | Solicitado: " + quantidade);
                }
                estoque.setQuantidadeAtual(quantidadeAtual.subtract(quantidade));
                EstoqueObra estoqueAtualizado = estoqueObraRepository.save(estoque);
                movimentacaoEstoqueService.registrarSaidaEstoque(
                        estoqueAtualizado, quantidade,
                        "Saída via comando de voz: " + nomeBuscado);
                return estoqueAtualizado;
            } else {
                // ENTRADA: soma ao saldo existente
                return consolidarEntrada(estoque, quantidade, null,
                        "Entrada via comando de voz: " + nomeBuscado);
            }
        }

        // Novo item no estoque — apenas entradas criam registro
        if (isSubtrair) {
            throw new BusinessException(
                    "Não há estoque cadastrado para '" + material.getNome() + "' nesta obra. " +
                    "Realize primeiro uma entrada para criar o item no almoxarifado.");
        }

        EstoqueObra novoEstoque = EstoqueObra.builder()
                .obra(obra)
                .material(material)
                .fornecedor(material.getFornecedor())
                .quantidadeAtual(quantidade)
                .quantidadeMinima(BigDecimal.ZERO)
                .build();

        EstoqueObra estoquesSalvo = estoqueObraRepository.save(novoEstoque);
        EstoqueObra estoqueCompleto = estoqueObraRepository
                .findByIdWithObraAndMaterial(estoquesSalvo.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado após criação."));
        movimentacaoEstoqueService.registrarEntradaEstoque(
                estoqueCompleto, quantidade,
                "Entrada via comando de voz: " + nomeBuscado);
        return estoqueCompleto;
    }

    /**
     * Detecta se a ação transcrita representa uma SAÍDA/subtração de estoque.
     * <p>
     * Tolera: maiúsculas/minúsculas, acentos, strings parciais como "Nao identificada",
     * palavras compostas e qualquer variação que o microsserviço possa retornar.
     * <p>
     * Raízes de subtração (PT-BR): subtra, retir, remov, gast, consum, said, saíd,
     *   usar, us, perd, diminui, descont, baixa, debit, dar saída
     * <p>
     * Raízes de adição (PT-BR): adicion, entr, receb, compr, repos, repor, repon,
     *   inserir, incluir, coloc, cheg, aument, cred, dar entrada
     * <p>
     * Regra: se nenhuma raiz for detectada → assume ENTRADA (comportamento padrão seguro).
     */
    private boolean detectarSubtracao(String acao) {
        if (acao == null || acao.isBlank()) {
            return false; // padrão: ENTRADA
        }
        String lower = acao.toLowerCase()
                .replace("ã", "a").replace("â", "a").replace("á", "a").replace("à", "a")
                .replace("é", "e").replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o").replace("ô", "o")
                .replace("ú", "u").replace("ç", "c");

        // Raízes que indicam SAÍDA
        String[] saidaRoots = {
            "subtra", "retir", "remov", "gast", "consum",
            "said", "said", "usar", " us ", "perd", "diminu",
            "descont", "baixa", "debit", "saida", "retirad",
            "consumo", "uso ", "utilizad", "descontad"
        };

        for (String root : saidaRoots) {
            if (lower.contains(root)) {
                return true; // SAÍDA detectada
            }
        }
        return false; // padrão: ENTRADA
    }

    private String removerAcentos(String str) {
        if (str == null) return null;
        String normalizado = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{M}", "");
    }
}
