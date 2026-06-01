package com.example.gestaoobraapi.security;

public final class PermissaoCodigo {

    public static final String RELATORIO_OBRA_EXPORTAR = "RELATORIO_OBRA_EXPORTAR";
    public static final String RELATORIO_MOVIMENTACAO_LISTAR = "RELATORIO_MOVIMENTACAO_LISTAR";

    /** Permissões já concedidas a ADMIN/MESTRE_OBRAS na V0004 (retrocompatibilidade). */
    public static final String OBRA_LISTAR = "OBRA_LISTAR";
    public static final String MOVIMENTACAO_ESTOQUE_LISTAR = "MOVIMENTACAO_ESTOQUE_LISTAR";

    private PermissaoCodigo() {
    }
}
