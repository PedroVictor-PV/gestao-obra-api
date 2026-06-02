-- ============================================================
-- V6 - Cargo, funcionário, vínculos com obra e fornecedor por obra
-- ============================================================

CREATE TABLE public.cargo (
    id            BIGSERIAL    PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL UNIQUE,
    descricao     TEXT,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT       REFERENCES seguranca.usuario(id),
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT       REFERENCES seguranca.usuario(id),
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE public.funcionario (
    id            BIGSERIAL    PRIMARY KEY,
    nome          VARCHAR(150) NOT NULL,
    telefone      VARCHAR(20),
    id_cargo      BIGINT       NOT NULL REFERENCES public.cargo(id),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT       REFERENCES seguranca.usuario(id),
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT       REFERENCES seguranca.usuario(id),
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE public.obra_funcionario (
    id              BIGSERIAL PRIMARY KEY,
    id_obra         BIGINT    NOT NULL REFERENCES public.obra(id),
    id_funcionario  BIGINT    NOT NULL REFERENCES public.funcionario(id),
    ativo           BOOLEAN   NOT NULL DEFAULT TRUE,
    criado_por      BIGINT    REFERENCES seguranca.usuario(id),
    criado_em       TIMESTAMP NOT NULL DEFAULT NOW(),
    alterado_por    BIGINT    REFERENCES seguranca.usuario(id),
    alterado_em     TIMESTAMP NOT NULL DEFAULT NOW()
    
);


ALTER TABLE public.fornecedor
    ADD COLUMN id_obra BIGINT REFERENCES public.obra(id);

UPDATE public.fornecedor f
SET id_obra = (SELECT o.id FROM public.obra o ORDER BY o.id LIMIT 1)
WHERE f.id_obra IS NULL;

ALTER TABLE public.fornecedor
    ALTER COLUMN id_obra SET NOT NULL;

ALTER TABLE public.fornecedor
    DROP CONSTRAINT IF EXISTS fornecedor_codigo_key;

ALTER TABLE public.fornecedor
    ADD CONSTRAINT fornecedor_id_obra_codigo_key UNIQUE (id_obra, codigo);
