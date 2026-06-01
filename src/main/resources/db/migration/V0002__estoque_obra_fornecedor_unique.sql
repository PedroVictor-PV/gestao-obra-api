-- ============================================================
-- V2 - Estoque por obra: fornecedor e unicidade composta
-- Aplica em bases criadas antes da constraint (id_obra, id_material, id_fornecedor).
-- Bases novas (V0001 atualizada) não são alteradas de forma destrutiva.
-- ============================================================

ALTER TABLE public.estoque_obra
    ADD COLUMN IF NOT EXISTS id_fornecedor BIGINT REFERENCES public.fornecedor(id);

UPDATE public.estoque_obra eo
SET id_fornecedor = m.id_fornecedor
FROM public.material m
WHERE eo.id_material = m.id
  AND eo.id_fornecedor IS NULL;

ALTER TABLE public.estoque_obra
    ALTER COLUMN id_fornecedor SET NOT NULL;

ALTER TABLE public.estoque_obra
    DROP CONSTRAINT IF EXISTS estoque_obra_id_obra_id_material_key;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint c
        JOIN pg_class t ON c.conrelid = t.oid
        JOIN pg_namespace n ON t.relnamespace = n.oid
        WHERE n.nspname = 'public'
          AND t.relname = 'estoque_obra'
          AND c.contype = 'u'
          AND pg_get_constraintdef(c.oid) LIKE '%(id_obra, id_material, id_fornecedor)%'
    ) THEN
        ALTER TABLE public.estoque_obra
            ADD CONSTRAINT estoque_obra_id_obra_id_material_id_fornecedor_key
            UNIQUE (id_obra, id_material, id_fornecedor);
    END IF;
END $$;
