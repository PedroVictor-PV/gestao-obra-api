-- Garante permissões de relatório para perfis administrativos (idempotente)

INSERT INTO seguranca.permissao (codigo, nome, descricao) VALUES
    ('RELATORIO_OBRA_EXPORTAR',       'Exportar planilha da obra',        'Gera arquivo Excel consolidado da obra'),
    ('RELATORIO_MOVIMENTACAO_LISTAR', 'Listar movimentações com filtros', 'Consulta movimentações de estoque com filtros opcionais')
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO seguranca.perfil_permissao (id_perfil, id_permissao)
SELECT p.id, perm.id
FROM seguranca.perfil p
CROSS JOIN seguranca.permissao perm
WHERE p.codigo IN ('ADMIN', 'MESTRE_OBRAS')
  AND perm.codigo IN ('RELATORIO_OBRA_EXPORTAR', 'RELATORIO_MOVIMENTACAO_LISTAR')
ON CONFLICT (id_perfil, id_permissao) DO NOTHING;
