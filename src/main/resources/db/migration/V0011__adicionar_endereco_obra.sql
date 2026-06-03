-- ============================================================
-- V0011 - Adicionar campos de endereço (padrão ViaCEP) na obra
-- ============================================================

ALTER TABLE public.obra
    ADD COLUMN cep          VARCHAR(9),
    ADD COLUMN logradouro   VARCHAR(255),
    ADD COLUMN numero       VARCHAR(20),
    ADD COLUMN complemento  VARCHAR(255),
    ADD COLUMN bairro       VARCHAR(150),
    ADD COLUMN localidade   VARCHAR(150),
    ADD COLUMN uf           VARCHAR(2);

-- Endereço padrão de demonstração para OBR-001 (Praça da Sé - SP)
UPDATE public.obra
SET cep         = '01001-000',
    logradouro  = 'Praça da Sé',
    numero      = '100',
    complemento = 'Bloco A',
    bairro      = 'Sé',
    localidade  = 'São Paulo',
    uf          = 'SP'
WHERE codigo = 'OBR-001';
