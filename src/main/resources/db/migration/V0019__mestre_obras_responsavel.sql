-- ============================================================
-- V0019 - Mestre de Obras como responsável por todas as obras
-- ============================================================

UPDATE public.obra
SET id_responsavel = (SELECT id FROM seguranca.usuario WHERE chave = '22222222222' LIMIT 1)
WHERE codigo IN ('OBR-001', 'OBR-002', 'OBR-003');
