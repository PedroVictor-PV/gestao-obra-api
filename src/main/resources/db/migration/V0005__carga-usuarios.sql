-- ============================================================
-- V5 - Usuários de demonstração (senha: 123456, BCrypt)
-- Login administrador: 11111111111 | mestre de obras: 22222222222
-- ============================================================

INSERT INTO seguranca.usuario (chave, senha) VALUES
    ('11111111111', '$2a$10$YJzXG7yttW51iWoQA6YSo.G5opoj3km0oPFsVWR.4WA4v/iA3yiqi'),
    ('22222222222', '$2a$10$YJzXG7yttW51iWoQA6YSo.G5opoj3km0oPFsVWR.4WA4v/iA3yiqi')
ON CONFLICT (chave) DO NOTHING;

INSERT INTO seguranca.usuario_perfil (id_usuario, id_perfil)
SELECT u.id, p.id
FROM seguranca.usuario u
JOIN seguranca.perfil p ON p.codigo = 'ADMIN'
WHERE u.chave = '11111111111'
ON CONFLICT (id_usuario, id_perfil) DO NOTHING;

INSERT INTO seguranca.usuario_perfil (id_usuario, id_perfil)
SELECT u.id, p.id
FROM seguranca.usuario u
JOIN seguranca.perfil p ON p.codigo = 'MESTRE_OBRAS'
WHERE u.chave = '22222222222'
ON CONFLICT (id_usuario, id_perfil) DO NOTHING;
