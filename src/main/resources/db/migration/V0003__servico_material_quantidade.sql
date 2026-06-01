ALTER TABLE public.servico_material
    ADD COLUMN IF NOT EXISTS quantidade NUMERIC(12,3) NOT NULL DEFAULT 0;
