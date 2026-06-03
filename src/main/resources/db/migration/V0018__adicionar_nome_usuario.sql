-- ============================================================
-- V0018 - Adiciona coluna de nome na tabela de usuário
-- ============================================================

ALTER TABLE seguranca.usuario ADD COLUMN nome VARCHAR(150);

UPDATE seguranca.usuario SET nome = 'Ana Administradora' WHERE chave = '11111111111';
UPDATE seguranca.usuario SET nome = 'Bruno Mestre de Obras' WHERE chave = '22222222222';
UPDATE seguranca.usuario SET nome = 'Carlos Operário' WHERE chave = '33333333333';
UPDATE seguranca.usuario SET nome = 'Diego Operário OBR-002' WHERE chave = '44444444444';
