-- ============================================================
-- V0020 - Diversificar os mestres de obras (um com 2 obras, outro com 1)
-- ============================================================

-- 1. Inserir o novo usuário Mestre de Obras (chave: 55555555555 | senha: 123456)
INSERT INTO seguranca.usuario (chave, senha, nome)
VALUES ('55555555555', '$2a$10$YJzXG7yttW51iWoQA6YSo.G5opoj3km0oPFsVWR.4WA4v/iA3yiqi', 'Eduardo Mestre de Obras')
ON CONFLICT (chave) DO NOTHING;

-- 2. Vincular ao perfil de Mestre de Obras
INSERT INTO seguranca.usuario_perfil (id_usuario, id_perfil)
SELECT u.id, p.id
FROM seguranca.usuario u
JOIN seguranca.perfil p ON p.codigo = 'MESTRE_OBRAS'
WHERE u.chave = '55555555555'
ON CONFLICT (id_usuario, id_perfil) DO NOTHING;

-- 3. Criar o funcionário no banco
INSERT INTO public.funcionario (nome, telefone, id_cargo)
SELECT 'Eduardo Mestre de Obras', '(11) 90000-0005', c.id
FROM public.cargo c
WHERE c.nome = 'mestre_obras'
  AND NOT EXISTS (
      SELECT 1 FROM public.funcionario f WHERE f.nome = 'Eduardo Mestre de Obras'
  );

-- 4. Vincular o novo Mestre à OBR-003 (usuario_obra para JWT e obra_funcionario para equipe)
INSERT INTO seguranca.usuario_obra (id_usuario, id_obra)
SELECT u.id, o.id
FROM seguranca.usuario u, public.obra o
WHERE u.chave = '55555555555'
  AND o.codigo = 'OBR-003'
ON CONFLICT (id_usuario, id_obra) DO NOTHING;

INSERT INTO public.obra_funcionario (id_obra, id_funcionario)
SELECT o.id, f.id
FROM public.obra o
JOIN public.funcionario f ON f.nome = 'Eduardo Mestre de Obras'
WHERE o.codigo = 'OBR-003'
  AND NOT EXISTS (
      SELECT 1 FROM public.obra_funcionario of2
      WHERE of2.id_obra = o.id AND of2.id_funcionario = f.id
  );

-- 5. Atualizar responsabilidades das obras:
-- Bruno (22222222222) -> OBR-001 e OBR-002 (2 obras)
UPDATE public.obra
SET id_responsavel = (SELECT id FROM seguranca.usuario WHERE chave = '22222222222' LIMIT 1)
WHERE codigo IN ('OBR-001', 'OBR-002');

-- Eduardo (55555555555) -> OBR-003 (1 obra)
UPDATE public.obra
SET id_responsavel = (SELECT id FROM seguranca.usuario WHERE chave = '55555555555' LIMIT 1)
WHERE codigo = 'OBR-003';
