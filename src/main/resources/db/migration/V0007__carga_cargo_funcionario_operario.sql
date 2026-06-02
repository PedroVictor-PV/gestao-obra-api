-- ============================================================
-- V7 - Carga: cargos, funcionários, obra 1, perfil operário, fornecedores
-- ============================================================

INSERT INTO public.cargo (nome, descricao) VALUES
    ('administrador', 'Responsável administrativo da obra'),
    ('mestre_obras',  'Responsável técnico pela execução dos serviços'),
    ('operario',      'Execução operacional em campo')
ON CONFLICT (nome) DO NOTHING;

UPDATE public.fornecedor f
SET id_obra = 1
WHERE f.id_obra IS DISTINCT FROM 1
  AND EXISTS (SELECT 1 FROM public.obra o WHERE o.id = 1);

INSERT INTO public.funcionario (nome, telefone, id_cargo)
SELECT v.nome, v.telefone, c.id
FROM (VALUES
    ('Ana Administradora',     '(11) 90000-0001', 'administrador'),
    ('Bruno Mestre de Obras',  '(11) 90000-0002', 'mestre_obras'),
    ('Carlos Operário',        '(11) 90000-0003', 'operario')
) AS v(nome, telefone, cargo_nome)
JOIN public.cargo c ON c.nome = v.cargo_nome
WHERE NOT EXISTS (
    SELECT 1 FROM public.funcionario f WHERE f.nome = v.nome
);

INSERT INTO public.obra_funcionario (id_obra, id_funcionario)
SELECT 1, f.id
FROM public.funcionario f
WHERE f.nome IN ('Ana Administradora', 'Bruno Mestre de Obras', 'Carlos Operário')
  AND EXISTS (SELECT 1 FROM public.obra o WHERE o.id = 1)
  AND NOT EXISTS (                                 
      SELECT 1 FROM public.obra_funcionario of2
      WHERE of2.id_obra = 1
        AND of2.id_funcionario = f.id
  );

INSERT INTO seguranca.perfil (codigo, nome, descricao) VALUES
    ('OPERARIO', 'Operário', 'Leitura geral e entrada de estoque na obra')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO seguranca.perfil_permissao (id_perfil, id_permissao)
SELECT p.id, perm.id
FROM seguranca.perfil p
JOIN seguranca.permissao perm ON perm.codigo ~ '_LISTAR$'
WHERE p.codigo = 'OPERARIO'
ON CONFLICT (id_perfil, id_permissao) DO NOTHING;

INSERT INTO seguranca.perfil_permissao (id_perfil, id_permissao)
SELECT p.id, perm.id
FROM seguranca.perfil p
JOIN seguranca.permissao perm ON perm.codigo IN ('PERFIL_VISUALIZAR', 'PERMISSAO_VISUALIZAR', 'ESTOQUE_OBRA_CRIAR')
WHERE p.codigo = 'OPERARIO'
ON CONFLICT (id_perfil, id_permissao) DO NOTHING;

INSERT INTO seguranca.usuario (chave, senha) VALUES
    ('33333333333', '$2a$10$YJzXG7yttW51iWoQA6YSo.G5opoj3km0oPFsVWR.4WA4v/iA3yiqi')
ON CONFLICT (chave) DO NOTHING;

INSERT INTO seguranca.usuario_perfil (id_usuario, id_perfil)
SELECT u.id, p.id
FROM seguranca.usuario u
JOIN seguranca.perfil p ON p.codigo = 'OPERARIO'
WHERE u.chave = '33333333333'
ON CONFLICT (id_usuario, id_perfil) DO NOTHING;
