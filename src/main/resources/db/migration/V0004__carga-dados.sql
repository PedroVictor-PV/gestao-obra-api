-- ============================================================
-- V4 - Carga de dados (domínio + permissões)
-- V0001 já possui: status_obra, status_servico, perfil, categoria_material
-- ============================================================

-- ------------------------------------------------------------
-- Permissões por módulo (CRUD)
-- ------------------------------------------------------------

INSERT INTO seguranca.permissao (codigo, nome, descricao) VALUES
    ('FORNECEDOR_LISTAR',           'Listar fornecedores',              'Consulta de fornecedores'),
    ('FORNECEDOR_CRIAR',            'Criar fornecedor',                 'Cadastro de fornecedores'),
    ('FORNECEDOR_ATUALIZAR',        'Atualizar fornecedor',             'Alteração de fornecedores'),
    ('FORNECEDOR_EXCLUIR',          'Excluir fornecedor',               'Remoção de fornecedores'),
    ('OBRA_LISTAR',                 'Listar obras',                     'Consulta de obras'),
    ('OBRA_CRIAR',                  'Criar obra',                       'Cadastro de obras'),
    ('OBRA_ATUALIZAR',              'Atualizar obra',                   'Alteração de obras'),
    ('OBRA_EXCLUIR',                'Excluir obra',                     'Remoção de obras'),
    ('STATUS_OBRA_LISTAR',          'Listar status de obra',            'Consulta de status de obra'),
    ('STATUS_OBRA_CRIAR',           'Criar status de obra',             'Cadastro de status de obra'),
    ('STATUS_OBRA_ATUALIZAR',       'Atualizar status de obra',         'Alteração de status de obra'),
    ('STATUS_OBRA_EXCLUIR',         'Excluir status de obra',           'Remoção de status de obra'),
    ('CATEGORIA_MATERIAL_LISTAR',   'Listar categorias de material',    'Consulta de categorias'),
    ('CATEGORIA_MATERIAL_CRIAR',    'Criar categoria de material',      'Cadastro de categorias'),
    ('CATEGORIA_MATERIAL_ATUALIZAR','Atualizar categoria de material',  'Alteração de categorias'),
    ('CATEGORIA_MATERIAL_EXCLUIR',  'Excluir categoria de material',    'Remoção de categorias'),
    ('MATERIAL_LISTAR',             'Listar materiais',                 'Consulta de materiais'),
    ('MATERIAL_CRIAR',              'Criar material',                   'Cadastro de materiais'),
    ('MATERIAL_ATUALIZAR',          'Atualizar material',               'Alteração de materiais'),
    ('MATERIAL_EXCLUIR',            'Excluir material',                 'Remoção de materiais'),
    ('ESTOQUE_OBRA_LISTAR',         'Listar estoque da obra',           'Consulta de estoque por obra'),
    ('ESTOQUE_OBRA_CRIAR',          'Criar estoque da obra',            'Entrada de material no estoque'),
    ('ESTOQUE_OBRA_ATUALIZAR',      'Atualizar estoque da obra',        'Ajuste de estoque por obra'),
    ('ESTOQUE_OBRA_EXCLUIR',        'Excluir estoque da obra',          'Remoção de registro de estoque'),
    ('MOVIMENTACAO_ESTOQUE_LISTAR', 'Listar movimentações de estoque',  'Consulta de movimentações'),
    ('STATUS_SERVICO_LISTAR',       'Listar status de serviço',         'Consulta de status de serviço'),
    ('STATUS_SERVICO_CRIAR',        'Criar status de serviço',          'Cadastro de status de serviço'),
    ('STATUS_SERVICO_ATUALIZAR',    'Atualizar status de serviço',      'Alteração de status de serviço'),
    ('STATUS_SERVICO_EXCLUIR',      'Excluir status de serviço',        'Remoção de status de serviço'),
    ('SERVICO_LISTAR',              'Listar serviços',                  'Consulta de serviços da obra'),
    ('SERVICO_CRIAR',               'Criar serviço',                    'Cadastro de serviços'),
    ('SERVICO_ATUALIZAR',           'Atualizar serviço',                'Alteração de serviços'),
    ('SERVICO_EXCLUIR',             'Excluir serviço',                  'Remoção de serviços'),
    ('SERVICO_MATERIAL_LISTAR',     'Listar materiais do serviço',      'Consulta de destinação de materiais'),
    ('SERVICO_MATERIAL_CRIAR',      'Destinar material ao serviço',     'Alocação de material para serviço'),
    ('SERVICO_MATERIAL_ATUALIZAR',  'Atualizar destinação de material', 'Alteração de destinação'),
    ('SERVICO_MATERIAL_EXCLUIR',    'Excluir destinação de material',   'Remoção de destinação'),
    ('PERFIL_LISTAR',               'Listar perfis',                    'Consulta de perfis'),
    ('PERFIL_VISUALIZAR',           'Visualizar perfil',                'Detalhe de perfil'),
    ('PERMISSAO_LISTAR',            'Listar permissões',                'Consulta de permissões'),
    ('PERMISSAO_VISUALIZAR',        'Visualizar permissão',             'Detalhe de permissão'),
    ('PERFIL_PERMISSAO_LISTAR',     'Listar vínculos perfil-permissão', 'Consulta de vínculos')
ON CONFLICT (codigo) DO NOTHING;

-- Administrador e Mestre de obras recebem todas as permissões
INSERT INTO seguranca.perfil_permissao (id_perfil, id_permissao)
SELECT p.id, perm.id
FROM seguranca.perfil p
CROSS JOIN seguranca.permissao perm
WHERE p.codigo IN ('ADMIN', 'MESTRE_OBRAS')
ON CONFLICT (id_perfil, id_permissao) DO NOTHING;

-- ------------------------------------------------------------
-- Fornecedores
-- ------------------------------------------------------------

INSERT INTO public.fornecedor (codigo, nome, telefone) VALUES
    ('FORN-001', 'Construmax Materiais', '(11) 3000-1001'),
    ('FORN-002', 'HidroCenter',          '(11) 3000-1002'),
    ('FORN-003', 'EletroForte',          '(11) 3000-1003')
ON CONFLICT (codigo) DO NOTHING;

-- ------------------------------------------------------------
-- Obra
-- ------------------------------------------------------------

INSERT INTO public.obra (
    id_status_obra, codigo, nome, descricao, data_inicio
) VALUES (
    (SELECT id FROM public.status_obra WHERE codigo = 'EM_ANDAMENTO'),
    'OBR-001',
    'Residencial Jardim das Palmeiras',
    'Obra residencial multifamiliar - bloco A',
    CURRENT_DATE - INTERVAL '30 days'
)
ON CONFLICT (codigo) DO NOTHING;

-- ------------------------------------------------------------
-- Materiais
-- ------------------------------------------------------------

INSERT INTO public.material (id_categoria, id_fornecedor, codigo, nome) VALUES
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'ALVENARIA'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'FORN-001'),
        'MAT-001',
        'Cimento CP II 50kg'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'HIDRAULICO'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'FORN-002'),
        'MAT-002',
        'Tubo PVC 50mm'
    ),
    (
        (SELECT id FROM public.categoria_material WHERE codigo = 'ELETRICO'),
        (SELECT id FROM public.fornecedor WHERE codigo = 'FORN-003'),
        'MAT-003',
        'Cabo flexível 2,5mm'
    )
ON CONFLICT (codigo) DO NOTHING;

-- ------------------------------------------------------------
-- Estoque da obra (saldo inicial)
-- ------------------------------------------------------------

INSERT INTO public.estoque_obra (
    id_obra, id_material, id_fornecedor, quantidade_atual, quantidade_minima
)
SELECT
    o.id,
    m.id,
    m.id_fornecedor,
    v.quantidade_atual,
    v.quantidade_minima
FROM public.obra o
CROSS JOIN (VALUES
    ('MAT-001', 200.000, 50.000),
    ('MAT-002', 150.000, 30.000),
    ('MAT-003', 500.000, 100.000)
) AS v(codigo_material, quantidade_atual, quantidade_minima)
JOIN public.material m ON m.codigo = v.codigo_material
WHERE o.codigo = 'OBR-001'
ON CONFLICT (id_obra, id_material, id_fornecedor) DO NOTHING;

-- ------------------------------------------------------------
-- Movimentações (entrada inicial de estoque)
-- ------------------------------------------------------------

INSERT INTO public.movimentacao_estoque (
    id_obra, id_material, id_fornecedor, tipo, quantidade, observacao
)
SELECT
    eo.id_obra,
    eo.id_material,
    eo.id_fornecedor,
    'ENTRADA',
    eo.quantidade_atual,
    'Carga inicial - migration V0004'
FROM public.estoque_obra eo
JOIN public.obra o ON o.id = eo.id_obra
WHERE o.codigo = 'OBR-001'
  AND NOT EXISTS (
      SELECT 1
      FROM public.movimentacao_estoque me
      WHERE me.id_obra = eo.id_obra
        AND me.id_material = eo.id_material
        AND me.id_fornecedor = eo.id_fornecedor
        AND me.tipo = 'ENTRADA'
        AND me.observacao = 'Carga inicial - migration V0004'
  );

-- ------------------------------------------------------------
-- Serviço
-- ------------------------------------------------------------

INSERT INTO public.servico (
    id_obra, id_status_servico, nome, descricao, observacao
)
SELECT
    o.id,
    ss.id,
    'Alvenaria bloco A',
    'Execução de alvenaria do bloco A',
    'Serviço de demonstração'
FROM public.obra o
CROSS JOIN public.status_servico ss
WHERE o.codigo = 'OBR-001'
  AND ss.codigo = 'EM_ANDAMENTO'
  AND NOT EXISTS (
      SELECT 1 FROM public.servico s
      WHERE s.id_obra = o.id AND s.nome = 'Alvenaria bloco A'
  );

-- ------------------------------------------------------------
-- Material destinado ao serviço (+ saída de estoque)
-- ------------------------------------------------------------

DO $$
DECLARE
    v_id_servico   BIGINT;
    v_id_obra      BIGINT;
    v_id_material  BIGINT;
    v_id_fornecedor BIGINT;
    v_nome_servico VARCHAR(150);
    v_quantidade   NUMERIC(12,3) := 25.000;
BEGIN
    SELECT s.id, s.id_obra, s.nome
    INTO v_id_servico, v_id_obra, v_nome_servico
    FROM public.servico s
    JOIN public.obra o ON o.id = s.id_obra
    WHERE o.codigo = 'OBR-001'
      AND s.nome = 'Alvenaria bloco A'
    LIMIT 1;

    IF v_id_servico IS NULL THEN
        RETURN;
    END IF;

    SELECT m.id, m.id_fornecedor
    INTO v_id_material, v_id_fornecedor
    FROM public.material m
    WHERE m.codigo = 'MAT-001';

    IF EXISTS (
        SELECT 1 FROM public.servico_material sm
        WHERE sm.id_servico = v_id_servico
          AND sm.id_obra = v_id_obra
          AND sm.id_material = v_id_material
    ) THEN
        RETURN;
    END IF;

    INSERT INTO public.servico_material (
        id_servico, id_obra, id_material, quantidade, observacao
    ) VALUES (
        v_id_servico,
        v_id_obra,
        v_id_material,
        v_quantidade,
        'Destinação inicial - migration V0004'
    );

    UPDATE public.estoque_obra eo
    SET quantidade_atual = eo.quantidade_atual - v_quantidade
    WHERE eo.id_obra = v_id_obra
      AND eo.id_material = v_id_material
      AND eo.id_fornecedor = v_id_fornecedor;

    INSERT INTO public.movimentacao_estoque (
        id_obra, id_material, id_fornecedor, tipo, quantidade, observacao
    ) VALUES (
        v_id_obra,
        v_id_material,
        v_id_fornecedor,
        'SAIDA',
        v_quantidade,
        'Enviada para serviço: ' || v_nome_servico
    );
END $$;
