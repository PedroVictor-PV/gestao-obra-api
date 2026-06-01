-- ============================================================
-- V1 - Carga inicial
-- Schema seguranca: usuarios, perfis e permissoes
-- Schema public:    fornecedor, obras, materiais, servicos
-- ============================================================

CREATE SCHEMA IF NOT EXISTS seguranca;

-- ============================================================
-- SCHEMA SEGURANCA
-- ============================================================

CREATE TABLE seguranca.usuario (
    id            BIGSERIAL    PRIMARY KEY,
    chave         VARCHAR(100) NOT NULL UNIQUE,
    senha         VARCHAR(255) NOT NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT,
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT,
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE seguranca.perfil (
    id            BIGSERIAL    PRIMARY KEY,
    codigo        VARCHAR(50)  NOT NULL UNIQUE,
    nome          VARCHAR(100) NOT NULL,
    descricao     TEXT,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT,
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT,
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE seguranca.permissao (
    id            BIGSERIAL    PRIMARY KEY,
    codigo        VARCHAR(100) NOT NULL UNIQUE,
    nome          VARCHAR(150) NOT NULL,
    descricao     TEXT,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT,
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT,
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE seguranca.perfil_permissao (
    id            BIGSERIAL PRIMARY KEY,
    id_perfil     BIGINT    NOT NULL REFERENCES seguranca.perfil(id),
    id_permissao  BIGINT    NOT NULL REFERENCES seguranca.permissao(id),
    ativo         BOOLEAN   NOT NULL DEFAULT TRUE,
    criado_por    BIGINT,
    criado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT,
    alterado_em   TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (id_perfil, id_permissao)
);

CREATE TABLE seguranca.usuario_perfil (
    id            BIGSERIAL PRIMARY KEY,
    id_usuario    BIGINT    NOT NULL REFERENCES seguranca.usuario(id),
    id_perfil     BIGINT    NOT NULL REFERENCES seguranca.perfil(id),
    ativo         BOOLEAN   NOT NULL DEFAULT TRUE,
    criado_por    BIGINT,
    criado_em     TIMESTAMP NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT,
    alterado_em   TIMESTAMP NOT NULL DEFAULT NOW(),
UNIQUE (id_usuario, id_perfil)
);

-- ============================================================
-- SCHEMA PUBLIC
-- ============================================================

-- ------------------------------------------------------------
-- Fornecedor
-- ------------------------------------------------------------

CREATE TABLE public.fornecedor (
    id            BIGSERIAL    PRIMARY KEY,
    codigo        VARCHAR(50)  NOT NULL UNIQUE,
    nome          VARCHAR(150) NOT NULL,
    telefone      VARCHAR(20),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT       REFERENCES seguranca.usuario(id),
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT       REFERENCES seguranca.usuario(id),
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Obras
-- ------------------------------------------------------------

CREATE TABLE public.status_obra (
    id            BIGSERIAL    PRIMARY KEY,
    codigo        VARCHAR(50)  NOT NULL UNIQUE,
    nome          VARCHAR(100) NOT NULL,
    descricao     TEXT,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT       REFERENCES seguranca.usuario(id),
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT       REFERENCES seguranca.usuario(id),
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE public.obra (
    id               BIGSERIAL    PRIMARY KEY,
    id_status_obra   BIGINT       NOT NULL REFERENCES public.status_obra(id),
    id_responsavel   BIGINT       REFERENCES seguranca.usuario(id),
    codigo           VARCHAR(50)  NOT NULL UNIQUE,
    nome             VARCHAR(150) NOT NULL,
    descricao        TEXT,
    data_inicio      DATE,
    data_fim         DATE,
    ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por       BIGINT       REFERENCES seguranca.usuario(id),
    criado_em        TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por     BIGINT       REFERENCES seguranca.usuario(id),
    alterado_em      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Materiais e Estoque
-- ------------------------------------------------------------

CREATE TABLE public.categoria_material (
    id            BIGSERIAL    PRIMARY KEY,
    codigo        VARCHAR(50)  NOT NULL UNIQUE,
    nome          VARCHAR(100) NOT NULL,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT       REFERENCES seguranca.usuario(id),
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT       REFERENCES seguranca.usuario(id),
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE public.material (
    id              BIGSERIAL     PRIMARY KEY,
    id_categoria    BIGINT        NOT NULL REFERENCES public.categoria_material(id),
    id_fornecedor   BIGINT        NOT NULL REFERENCES public.fornecedor(id),
    codigo          VARCHAR(50)   NOT NULL UNIQUE,
    nome            VARCHAR(150)  NOT NULL,
    ativo           BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_por      BIGINT        REFERENCES seguranca.usuario(id),
    criado_em       TIMESTAMP     NOT NULL DEFAULT NOW(),
    alterado_por    BIGINT        REFERENCES seguranca.usuario(id),
    alterado_em     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE public.estoque_obra (
    id                BIGSERIAL     PRIMARY KEY,
    id_obra           BIGINT        NOT NULL REFERENCES public.obra(id),
    id_material       BIGINT        NOT NULL REFERENCES public.material(id),
    id_fornecedor     BIGINT        NOT NULL REFERENCES public.fornecedor(id),
    quantidade_atual  NUMERIC(12,3) NOT NULL DEFAULT 0,
    quantidade_minima NUMERIC(12,3) NOT NULL DEFAULT 0,
    ativo             BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_por        BIGINT        REFERENCES seguranca.usuario(id),
    criado_em         TIMESTAMP     NOT NULL DEFAULT NOW(),
    alterado_por      BIGINT        REFERENCES seguranca.usuario(id),
    alterado_em       TIMESTAMP     NOT NULL DEFAULT NOW(),
    UNIQUE (id_obra, id_material, id_fornecedor)
);

CREATE TABLE public.movimentacao_estoque (
    id                BIGSERIAL     PRIMARY KEY,
    id_obra           BIGINT        NOT NULL REFERENCES public.obra(id),
    id_material       BIGINT        NOT NULL REFERENCES public.material(id),
    id_fornecedor     BIGINT        REFERENCES public.fornecedor(id),
    tipo              VARCHAR(20)   NOT NULL CHECK (tipo IN ('ENTRADA', 'SAIDA', 'AJUSTE')),
    quantidade        NUMERIC(12,3) NOT NULL,
    data_movimentacao DATE          NOT NULL DEFAULT CURRENT_DATE,
    observacao        TEXT,
    ativo             BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_por        BIGINT        REFERENCES seguranca.usuario(id),
    criado_em         TIMESTAMP     NOT NULL DEFAULT NOW(),
    alterado_por      BIGINT        REFERENCES seguranca.usuario(id),
    alterado_em       TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Serviços
-- ------------------------------------------------------------

CREATE TABLE public.status_servico (
    id            BIGSERIAL    PRIMARY KEY,
    codigo        VARCHAR(50)  NOT NULL UNIQUE,
    nome          VARCHAR(100) NOT NULL,
    descricao     TEXT,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por    BIGINT       REFERENCES seguranca.usuario(id),
    criado_em     TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por  BIGINT       REFERENCES seguranca.usuario(id),
    alterado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE public.servico (
    id                 BIGSERIAL    PRIMARY KEY,
    id_obra            BIGINT       NOT NULL REFERENCES public.obra(id),
    id_status_servico  BIGINT       NOT NULL REFERENCES public.status_servico(id),
    nome               VARCHAR(150) NOT NULL,
    descricao          TEXT,
    observacao         TEXT,
    ativo              BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_por         BIGINT       REFERENCES seguranca.usuario(id),
    criado_em          TIMESTAMP    NOT NULL DEFAULT NOW(),
    alterado_por       BIGINT       REFERENCES seguranca.usuario(id),
    alterado_em        TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- servico_material representa os materiais destinados a um servico dentro de uma obra especifica.
-- a chave unica garante que o mesmo material nao seja destinado duas vezes ao mesmo servico na mesma obra.
CREATE TABLE public.servico_material (
    id           BIGSERIAL PRIMARY KEY,
    id_servico   BIGINT    NOT NULL REFERENCES public.servico(id),
    id_obra      BIGINT    NOT NULL REFERENCES public.obra(id),
    id_material  BIGINT    NOT NULL REFERENCES public.material(id),
    
    observacao   TEXT,
    UNIQUE (id_servico, id_obra, id_material)
);

-- ============================================================
-- SEEDS
-- ============================================================

INSERT INTO public.status_obra (codigo, nome, descricao) VALUES
     ('PLANEJADA',    'Planejada',    'Obra cadastrada, ainda não iniciada'),
     ('EM_ANDAMENTO', 'Em andamento', 'Obra em execução'),
     ('PARALISADA',   'Paralisada',   'Obra temporariamente suspensa'),
     ('CONCLUIDA',    'Concluída',    'Obra finalizada'),
     ('CANCELADA',    'Cancelada',    'Obra cancelada');

INSERT INTO public.status_servico (codigo, nome, descricao) VALUES
    ('PENDENTE',     'Pendente',     'Serviço aguardando início'),
    ('EM_ANDAMENTO', 'Em andamento', 'Serviço sendo executado'),
    ('CONCLUIDO',    'Concluído',    'Serviço finalizado'),
    ('CANCELADO',    'Cancelado',    'Serviço cancelado');

INSERT INTO seguranca.perfil (codigo, nome, descricao) VALUES
    ('ADMIN',        'Administrador',  'Acesso total ao sistema'),
    ('MESTRE_OBRAS', 'Mestre de obras','Gerencia obras e serviços');


INSERT INTO public.categoria_material (codigo, nome) VALUES
    ('HIDRAULICO',  'Hidráulico'),
    ('ELETRICO',    'Elétrico'),
    ('ESTRUTURA',   'Estrutura'),
    ('ALVENARIA',   'Alvenaria'),
    ('ACABAMENTO',  'Acabamento'),
    ('FERRAMENTAS', 'Ferramentas');

