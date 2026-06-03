-- ============================================================
-- V0012 - Tabela usuario_obra + dados de teste (2 obras)
-- ============================================================
-- Regra de negócio:
--   ADMIN      → acesso a todas as obras
--   MESTRE     → acesso a OBR-001 e OBR-002
--   OPERARIO   → acesso a apenas OBR-001
-- ============================================================

-- ------------------------------------------------------------
-- 1. Criação da tabela de vínculo usuario_obra
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS seguranca.usuario_obra (
    id            BIGSERIAL   PRIMARY KEY,
    id_usuario    BIGINT      NOT NULL REFERENCES seguranca.usuario(id),
    id_obra       BIGINT      NOT NULL REFERENCES public.obra(id),
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT usuario_obra_id_usuario_id_obra_key UNIQUE (id_usuario, id_obra)
);

-- ------------------------------------------------------------
-- 2. Garantir 2 obras de teste com id_responsavel
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

-- Define o responsável técnico de cada obra
UPDATE public.obra
SET id_responsavel = (SELECT id FROM seguranca.usuario WHERE chave = '11111111111' LIMIT 1)
WHERE codigo IN ('OBR-001', 'OBR-002');

-- ------------------------------------------------------------
-- 3. Vincular ADMIN (11111111111) → OBR-001 e OBR-002
-- ------------------------------------------------------------

INSERT INTO seguranca.usuario_obra (id_usuario, id_obra)
SELECT u.id, o.id
FROM seguranca.usuario u, public.obra o
WHERE u.chave = '11111111111'
  AND o.codigo IN ('OBR-001', 'OBR-002')
ON CONFLICT (id_usuario, id_obra) DO NOTHING;

-- ------------------------------------------------------------
-- 4. Vincular MESTRE (22222222222) → OBR-001 e OBR-002
-- ------------------------------------------------------------

INSERT INTO seguranca.usuario_obra (id_usuario, id_obra)
SELECT u.id, o.id
FROM seguranca.usuario u, public.obra o
WHERE u.chave = '22222222222'
  AND o.codigo IN ('OBR-001', 'OBR-002')
ON CONFLICT (id_usuario, id_obra) DO NOTHING;

-- ------------------------------------------------------------
-- 5. Vincular OPERARIO (33333333333) → apenas OBR-001
-- ------------------------------------------------------------

INSERT INTO seguranca.usuario_obra (id_usuario, id_obra)
SELECT u.id, o.id
FROM seguranca.usuario u, public.obra o
WHERE u.chave = '33333333333'
  AND o.codigo = 'OBR-001'
ON CONFLICT (id_usuario, id_obra) DO NOTHING;
