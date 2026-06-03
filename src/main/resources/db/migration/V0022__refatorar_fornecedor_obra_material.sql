-- ============================================================
-- V22 - Refatorar relacionamento Fornecedor <-> Obra para N:N
-- e vincular Material diretamente a Obra
-- ============================================================

-- 1. Criar a tabela associativa fornecedor_obra
CREATE TABLE public.fornecedor_obra (
    id_fornecedor BIGINT NOT NULL REFERENCES public.fornecedor(id) ON DELETE CASCADE,
    id_obra       BIGINT NOT NULL REFERENCES public.obra(id) ON DELETE CASCADE,
    PRIMARY KEY (id_fornecedor, id_obra)
);

-- 2. Migrar os dados existentes: Fornecedor -> Obra para a nova tabela
INSERT INTO public.fornecedor_obra (id_fornecedor, id_obra)
SELECT id, id_obra FROM public.fornecedor WHERE id_obra IS NOT NULL;

-- 3. Adicionar id_obra na tabela material
ALTER TABLE public.material ADD COLUMN id_obra BIGINT REFERENCES public.obra(id);

-- 4. Migrar os dados existentes: preencher material.id_obra usando o fornecedor atual
UPDATE public.material m
SET id_obra = (SELECT f.id_obra FROM public.fornecedor f WHERE f.id = m.id_fornecedor)
WHERE m.id_fornecedor IS NOT NULL;

-- Se houver algum material sem id_obra (não deveria), setar para a primeira obra existente
UPDATE public.material
SET id_obra = (SELECT id FROM public.obra ORDER BY id LIMIT 1)
WHERE id_obra IS NULL;

-- Tornar material.id_obra NOT NULL
ALTER TABLE public.material ALTER COLUMN id_obra SET NOT NULL;

-- 5. Remover a dependência de id_obra na tabela fornecedor
ALTER TABLE public.fornecedor DROP CONSTRAINT IF EXISTS fornecedor_id_obra_codigo_key;

-- Resolver possíveis duplicidades de código antes de criar a constraint UNIQUE
UPDATE public.fornecedor f1
SET codigo = codigo || '-' || id_obra
WHERE EXISTS (
    SELECT 1 FROM public.fornecedor f2 
    WHERE f1.codigo = f2.codigo AND f1.id <> f2.id
);

ALTER TABLE public.fornecedor ADD CONSTRAINT fornecedor_codigo_key UNIQUE (codigo);

ALTER TABLE public.fornecedor DROP COLUMN id_obra;
