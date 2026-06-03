-- ============================================================
-- V0017 - Vincular endereços reais às obras OBR-002 e OBR-003
-- ============================================================

-- Endereço para OBR-002 (Rio de Janeiro)
UPDATE public.obra
SET cep         = '20040-002',
    logradouro  = 'Avenida Rio Branco',
    numero      = '156',
    complemento = 'Edifício Avenida Central',
    bairro      = 'Centro',
    localidade  = 'Rio de Janeiro',
    uf          = 'RJ'
WHERE codigo = 'OBR-002';

-- Endereço para OBR-003 (Belo Horizonte)
UPDATE public.obra
SET cep         = '30140-120',
    logradouro  = 'Avenida Afonso Pena',
    numero      = '1000',
    complemento = 'Térreo',
    bairro      = 'Centro',
    localidade  = 'Belo Horizonte',
    uf          = 'MG'
WHERE codigo = 'OBR-003';
