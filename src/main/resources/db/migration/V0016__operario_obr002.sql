-- ============================================================
-- V0013 - Adiciona Operário 2 vinculado à OBR-002
-- ============================================================
-- Usuário: 44444444444 | senha: 123456

INSERT INTO seguranca.usuario (chave, senha)
VALUES ('44444444444', '$2a$10$YJzXG7yttW51iWoQA6YSo.G5opoj3km0oPFsVWR.4WA4v/iA3yiqi')
ON CONFLICT (chave) DO NOTHING;

-- Vincula ao perfil OPERARIO
INSERT INTO seguranca.usuario_perfil (id_usuario, id_perfil)
SELECT u.id, p.id
FROM seguranca.usuario u
JOIN seguranca.perfil p ON p.codigo = 'OPERARIO'
WHERE u.chave = '44444444444'
ON CONFLICT (id_usuario, id_perfil) DO NOTHING;

-- Vincula apenas à OBR-002
INSERT INTO seguranca.usuario_obra (id_usuario, id_obra)
SELECT u.id, o.id
FROM seguranca.usuario u, public.obra o
WHERE u.chave = '44444444444'
  AND o.codigo = 'OBR-002'
ON CONFLICT (id_usuario, id_obra) DO NOTHING;

-- Cria o funcionário operário e associa à OBR-002
INSERT INTO public.funcionario (nome, telefone, id_cargo)
SELECT 'Diego Operário OBR-002', '(11) 90000-0004', c.id
FROM public.cargo c
WHERE c.nome = 'operario'
  AND NOT EXISTS (
      SELECT 1 FROM public.funcionario f WHERE f.nome = 'Diego Operário OBR-002'
  );

INSERT INTO public.obra_funcionario (id_obra, id_funcionario)
SELECT o.id, f.id
FROM public.obra o
JOIN public.funcionario f ON f.nome = 'Diego Operário OBR-002'
WHERE o.codigo = 'OBR-002'
  AND NOT EXISTS (
      SELECT 1 FROM public.obra_funcionario of2
      WHERE of2.id_obra = o.id AND of2.id_funcionario = f.id
  );
