INSERT INTO seguranca.usuario_perfil
(id_usuario, id_perfil, ativo, criado_por, criado_em, alterado_por, alterado_em)
VALUES(3, 3, true, 1, NOW(), NULL, NOW())
ON CONFLICT (id_usuario, id_perfil) DO NOTHING;