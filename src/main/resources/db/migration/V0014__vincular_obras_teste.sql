-- ============================================================
-- V0011 - Setup completo de usuários de teste com obras vinculadas
-- ============================================================
-- Usuários:
--   Administrador : chave 11111111111 | senha 123456
--   Mestre de Obras: chave 22222222222 | senha 123456
--   Operário       : chave 33333333333 | senha 123456
-- ============================================================

-- ------------------------------------------------------------
-- 1. Garantir obras de teste (OBR-001, OBR-002, OBR-003)
-- ------------------------------------------------------------

INSERT INTO public.obra (id_status_obra, codigo, nome, descricao, data_inicio)
VALUES (
    (SELECT id FROM public.status_obra WHERE codigo = 'EM_ANDAMENTO'),
    'OBR-001',
    'Residencial Jardim das Palmeiras',
    'Obra residencial multifamiliar - bloco A',
    CURRENT_DATE - INTERVAL '60 days'
) ON CONFLICT (codigo) DO NOTHING;

INSERT INTO public.obra (id_status_obra, codigo, nome, descricao, data_inicio)
VALUES (
    (SELECT id FROM public.status_obra WHERE codigo = 'EM_ANDAMENTO'),
    'OBR-002',
    'Comercial Centro Empresarial Norte',
    'Obra comercial com 10 andares',
    CURRENT_DATE - INTERVAL '30 days'
) ON CONFLICT (codigo) DO NOTHING;

INSERT INTO public.obra (id_status_obra, codigo, nome, descricao, data_inicio)
VALUES (
    (SELECT id FROM public.status_obra WHERE codigo = 'EM_ANDAMENTO'),
    'OBR-003',
    'Condomínio Belo Horizonte',
    'Obra residencial com 20 unidades',
    CURRENT_DATE - INTERVAL '15 days'
) ON CONFLICT (codigo) DO NOTHING;

-- ------------------------------------------------------------
-- 2. Administrador (11111111111) → responsável por TODAS as obras
-- ------------------------------------------------------------

UPDATE public.obra
SET id_responsavel = (SELECT id FROM seguranca.usuario WHERE chave = '11111111111' LIMIT 1)
WHERE codigo IN ('OBR-001', 'OBR-002', 'OBR-003')
  AND id_responsavel IS NULL;

-- ------------------------------------------------------------
-- 3. Mestre de Obras (22222222222) → responsável por OBR-001 e OBR-002
-- ------------------------------------------------------------
-- Mestre de obras fica como co-responsável registrado em obra_funcionario

INSERT INTO public.obra_funcionario (id_obra, id_funcionario)
SELECT o.id, f.id
FROM public.obra o
JOIN public.funcionario f ON f.nome = 'Bruno Mestre de Obras'
WHERE o.codigo IN ('OBR-001', 'OBR-002')
  AND NOT EXISTS (
      SELECT 1 FROM public.obra_funcionario of2
      WHERE of2.id_obra = o.id AND of2.id_funcionario = f.id
  );

-- Vincula usuário 22222222222 como responsável adicional nas obras OBR-001 e OBR-002
-- via tabela usuario_obra (vínculo direto para o JWT)
-- Como a tabela obra só tem um id_responsavel, criamos uma segunda obra de responsabilidade
-- setando o campo apenas para obras onde o admin ainda não foi setado como responsável

-- Garante que id_responsavel da OBR-001 e OBR-002 seja o Mestre de Obras
-- (sobrescreve a regra acima para refletir a responsabilidade operacional)
UPDATE public.obra
SET id_responsavel = (SELECT id FROM seguranca.usuario WHERE chave = '22222222222' LIMIT 1)
WHERE codigo IN ('OBR-001', 'OBR-002');

-- Mantém OBR-003 para o Administrador
UPDATE public.obra
SET id_responsavel = (SELECT id FROM seguranca.usuario WHERE chave = '11111111111' LIMIT 1)
WHERE codigo = 'OBR-003';

-- ------------------------------------------------------------
-- 4. Operário (33333333333) → vinculado apenas à OBR-001
-- ------------------------------------------------------------

INSERT INTO public.obra_funcionario (id_obra, id_funcionario)
SELECT o.id, f.id
FROM public.obra o
JOIN public.funcionario f ON f.nome = 'Carlos Operário'
WHERE o.codigo = 'OBR-001'
  AND NOT EXISTS (
      SELECT 1 FROM public.obra_funcionario of2
      WHERE of2.id_obra = o.id AND of2.id_funcionario = f.id
  );
