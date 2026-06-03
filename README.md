# Gestão Obra API

API RESTful para gestão de obras de construção civil, controle de estoque de materiais, serviços, fornecedores e relatórios. Desenvolvida com Spring Boot 3 e Java 21.

---

## Visão Geral

O **Gestão Obra API** é um sistema backend voltado ao gerenciamento completo de obras civis. Ele permite controlar obras, seus estoques de materiais, movimentações, serviços executados, fornecedores vinculados e usuários com diferentes níveis de acesso (Administrador, Mestre de Obras e Operário).

Entre os diferenciais do projeto estão:

- Controle de acesso por perfil (RBAC) via JWT
- Rastreabilidade completa das movimentações de estoque
- Exportação de planilhas Excel (.xlsx) e arquivos CSV com dados consolidados por obra
- Endpoint especial para processamento de comandos de voz transcritos (integração com microsserviço Python)
- Vínculo dinâmico entre obras, usuários, materiais e fornecedores

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.4.6 | Framework web e DI |
| Spring Security | — | Autenticação e autorização |
| Spring Data JPA | — | Persistência de dados |
| Spring Cloud OpenFeign | 2024.0.0 | Integração com serviços externos |
| PostgreSQL | latest | Banco de dados relacional |
| Flyway | — | Migrations e versionamento do banco |
| JJWT | 0.12.5 | Geração e validação de tokens JWT |
| MapStruct | 1.5.5 | Mapeamento entre DTOs e entidades |
| Lombok | — | Redução de boilerplate |
| Apache POI | 5.2.5 | Geração de planilhas Excel (.xlsx) |
| SpringDoc OpenAPI | 2.8.5 | Documentação Swagger UI |
| Docker / Docker Compose | — | Containerização do PostgreSQL em desenvolvimento |
| Maven | 3.9.15 | Build e gerenciamento de dependências |

---

## Arquitetura

O projeto segue uma arquitetura em camadas clássica do Spring Boot:

```
Controller  →  Service  →  Repository  →  Database (PostgreSQL)
     ↕               ↕
   DTOs          Entities (JPA)
     ↕
  Mapper (MapStruct)
```

**Componentes transversais:**

- `BaseEntity` — superclasse com campos de auditoria (`criadoEm`, `alteradoEm`, `criadoPor`, `alteradoPor`, `ativo`)
- `GlobalExceptionHandler` — tratamento centralizado de exceções
- `JwtAuthenticationFilter` — filtro de autenticação via token Bearer
- `SecurityConfig` — configuração de CORS, CSRF e regras de acesso por rota

**Schemas do banco de dados:**

- `public` — domínio da obra: obras, materiais, estoque, serviços, fornecedores
- `seguranca` — domínio de segurança: usuários, perfis, permissões, vínculos

---

## Pré-requisitos

- Java 21+
- Maven 3.9+ (ou use o wrapper `./mvnw`)
- Docker e Docker Compose (para o banco de dados local)

---

## Configuração e Execução

### 1. Subir o banco de dados com Docker

```bash
cd local
docker-compose up -d
```

Isso inicia um container PostgreSQL na porta `5438` com o banco `gestaoobraapi` e senha `postgres`.

### 2. Compilar e executar a aplicação

```bash
./mvnw spring-boot:run
```

Ou para gerar o JAR e executar:

```bash
./mvnw clean package -DskipTests
java -jar target/gestaoobraapi-0.0.1-SNAPSHOT.jar
```

A API ficará disponível em: `http://localhost:8080`

### 3. Migrations automáticas

Ao iniciar, o Flyway executa automaticamente todos os scripts de migration em `src/main/resources/db/migration`, criando as tabelas e populando os dados de demonstração.

---

## Estrutura do Projeto

```
gestaoobraapi/
├── local/
│   └── docker-compose.yml              # PostgreSQL para desenvolvimento
├── src/
│   ├── main/
│   │   ├── java/com/example/gestaoobraapi/
│   │   │   ├── config/
│   │   │   │   ├── jwt/                # JwtUtil + JwtAuthenticationFilter
│   │   │   │   └── security/           # SecurityConfig + UsuarioDetailsService
│   │   │   ├── controller/             # Endpoints REST
│   │   │   ├── dto/                    # Request/Response gerados pelo OpenAPI Generator
│   │   │   ├── exception/              # Exceções customizadas + GlobalExceptionHandler
│   │   │   ├── mapper/                 # Interfaces MapStruct
│   │   │   ├── model/                  # Entidades JPA
│   │   │   ├── repository/             # Interfaces Spring Data JPA
│   │   │   ├── security/               # PermissaoCodigo (constantes)
│   │   │   └── service/                # Lógica de negócio
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── db/migration/           # Scripts Flyway (V0001 a V0023)
│   │       └── swagger/
│   │           └── gestaoobrasapi.json # Contrato OpenAPI (fonte dos DTOs gerados)
│   └── test/
└── pom.xml
```

---

## Módulos e Funcionalidades

### Obras (`/obra`)
Cadastro e gerenciamento de obras com endereço completo (padrão ViaCEP), status, responsável e controle de acesso por usuário vinculado.

### Status da Obra (`/obra/status`)
Domínio de status das obras: Planejada, Em Andamento, Paralisada, Concluída, Cancelada.

### Fornecedores (`/fornecedores`)
Cadastro de fornecedores com vínculo N:N a obras. Um fornecedor pode atender múltiplas obras.

### Materiais e Categorias (`/materiais`)
Cadastro de materiais com categoria, fornecedor e vínculo a obras via tabela `material_obra`.

### Estoque por Obra (`/obra/estoque`)
Controle de estoque de materiais por obra e fornecedor. Toda entrada ou saída registra uma movimentação automática.

### Movimentações de Estoque (`/obra/movimentacao/estoque/{idObra}`)
Log imutável de todas as entradas, saídas e ajustes de estoque. Também acessível via relatório filtrado.

### Serviços (`/servico`)
Serviços vinculados a obras com status próprio (Pendente, Em Andamento, Concluído, Cancelado).

### Material por Serviço (`/servico/material`)
Destinação de materiais do estoque para serviços. Ao destinar, a quantidade é descontada automaticamente do estoque e uma movimentação de saída é gerada.

### Perfis e Permissões (`/perfis`, `/permissoes`, `/perfil-permissoes`)
Consulta dos perfis (ADMIN, MESTRE_OBRAS, OPERARIO) e permissões configuradas no sistema.

### Usuários (`/usuarios`)
Listagem e criação de operários. Criação automática com vínculo ao perfil OPERARIO.

### Relatórios (`/relatorios`)
Exportação de planilhas Excel e CSV — veja a seção [Relatórios e Exportação](#relatórios-e-exportação).

---

## Segurança e Autenticação

### Autenticação

A API utiliza **JWT (JSON Web Token)** para autenticação stateless.

**Fluxo:**
1. `POST /auth/register` — cria um novo usuário
2. `POST /auth/login` — retorna um token JWT válido por **24 horas**

**Uso do token:**
```
Authorization: Bearer <token>
```

O token inclui os claims:
- `sub` — chave do usuário
- `obras` — lista de IDs de obras às quais o usuário tem acesso
- `nome` — nome de exibição
- `cargo` — Administrador, Mestre de Obras ou Funcionário

### Controle de Acesso (RBAC)

| Perfil | Acesso às obras | Permissões especiais |
|---|---|---|
| `ADMIN` | Todas as obras | Todas as permissões |
| `MESTRE_OBRAS` | Obras onde é responsável ou vinculado | Todas as permissões (exceto admin de usuários) |
| `OPERARIO` | Apenas obras vinculadas via `usuario_obra` | Leitura geral + entrada de estoque |

O filtro por obra é aplicado automaticamente nos endpoints de listagem conforme o perfil do usuário autenticado.

### CORS

Configurado para aceitar requisições de:
- `http://localhost:5173`
- `http://127.0.0.1:5173`

---

## Banco de Dados e Migrations

As migrations Flyway estão em `src/main/resources/db/migration/` e são executadas na ordem:

| Versão | Descrição |
|---|---|
| V0001 | Schema inicial: tabelas, seeds de status e perfis |
| V0002 | Unicidade composta no estoque por obra + fornecedor |
| V0003 | Adição de quantidade em `servico_material` |
| V0004 | Carga de dados: permissões, fornecedores, obra, materiais, estoque, serviço |
| V0005 | Usuários de demonstração com senha BCrypt |
| V0006 | Tabelas de cargo, funcionário e vínculo fornecedor-obra |
| V0007 | Carga de funcionários, perfil Operário e usuário operário |
| V0008 / V0009 | Permissões de relatórios |
| V0010 | Vínculo de perfil para usuário operário |
| V0011 | Campos de endereço (CEP, logradouro, bairro, etc.) na obra |
| V0014 | Setup de obras OBR-001, OBR-002, OBR-003 e vínculos |
| V0015 | Tabela `usuario_obra` e vínculos dos usuários de teste |
| V0016 | Operário adicional vinculado à OBR-002 |
| V0017 | Endereços das obras OBR-002 e OBR-003 |
| V0018 | Coluna `nome` na tabela de usuário |
| V0019 / V0020 | Responsáveis por obras e diversificação de mestres |
| V0021 | Limpeza de vínculos indevidos |
| V0022 | Refatoração para N:N entre Fornecedor e Obra |
| V0023 | Tabela `material_obra` (N:N entre Material e Obra) |

---

## Usuários de Demonstração

Todos com senha `123456`:

| Chave | Nome | Perfil | Obras |
|---|---|---|---|
| `11111111111` | Ana Administradora | ADMIN | Todas |
| `22222222222` | Bruno Mestre de Obras | MESTRE_OBRAS | OBR-001, OBR-002 |
| `33333333333` | Carlos Operário | OPERARIO | OBR-001 |
| `44444444444` | Diego Operário OBR-002 | OPERARIO | OBR-002 |
| `55555555555` | Eduardo Mestre de Obras | MESTRE_OBRAS | OBR-003 |

---

## Endpoints da API

### Autenticação

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/auth/register` | Registrar novo usuário | Não |
| POST | `/auth/login` | Login e obtenção do token JWT | Não |

### Obras

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/obra` | Cadastrar obra |
| GET | `/obra` | Listar obras (filtrado por perfil) |
| GET | `/obra/{id}` | Buscar obra por ID |
| PUT | `/obra/{id}` | Atualizar obra |
| DELETE | `/obra/{id}` | Excluir obra |
| GET | `/obra/mestres` | Listar usuários com perfil Mestre de Obras |
| GET | `/obras/{idObra}/operadores` | Listar operadores vinculados à obra |
| POST | `/obras/{idObra}/operadores/{idOperador}` | Vincular operador à obra |
| DELETE | `/obras/{idObra}/operadores/{idOperador}` | Desvincular operador da obra |

### Status da Obra

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/obra/status` | Criar status |
| GET | `/obra/status` | Listar todos os status |
| GET | `/obra/status/{id}` | Buscar por ID |
| PUT | `/obra/status/{id}` | Atualizar |
| DELETE | `/obra/status/{id}` | Excluir |

### Fornecedores

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/fornecedores` | Cadastrar fornecedor |
| GET | `/fornecedores` | Listar fornecedores |
| GET | `/fornecedores/{id}` | Buscar por ID |
| PUT | `/fornecedores/{id}` | Atualizar |
| DELETE | `/fornecedores/{id}` | Excluir |

### Materiais

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/materiais` | Cadastrar material |
| GET | `/materiais` | Listar materiais |
| GET | `/materiais/{id}` | Buscar por ID |
| PUT | `/materiais/{id}` | Atualizar |
| DELETE | `/materiais/{id}` | Excluir |
| POST | `/materiais/categoria` | Criar categoria |
| GET | `/materiais/categoria` | Listar categorias |
| GET | `/materiais/categoria/{id}` | Buscar categoria por ID |
| PUT | `/materiais/categoria/{id}` | Atualizar categoria |
| DELETE | `/materiais/categoria/{id}` | Excluir categoria |

### Estoque

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/obra/estoque` | Criar/consolidar entrada de estoque |
| GET | `/obra/estoque` | Listar todo o estoque |
| GET | `/obra/estoque/{id}` | Buscar por ID |
| PUT | `/obra/estoque/{id}` | Atualizar estoque (gera ajuste) |
| DELETE | `/obra/estoque/{id}` | Excluir (gera saída) |
| POST | `/obra/estoque/voz` | Entrada por comando de voz |
| GET | `/obra/movimentacao/estoque/{idObra}` | Listar movimentações por obra |

### Serviços

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/servico` | Criar serviço |
| GET | `/servico` | Listar serviços |
| GET | `/servico/{id}` | Buscar por ID |
| PUT | `/servico/{id}` | Atualizar |
| DELETE | `/servico/{id}` | Excluir |
| POST | `/servico/status` | Criar status de serviço |
| GET | `/servico/status` | Listar status |
| GET | `/servico/status/{id}` | Buscar por ID |
| PUT | `/servico/status/{id}` | Atualizar |
| DELETE | `/servico/status/{id}` | Excluir |
| POST | `/servico/material` | Destinar material ao serviço |
| GET | `/servico/material` | Listar destinações |
| GET | `/servico/material/{id}` | Buscar destinação por ID |
| PUT | `/servico/material/{id}` | Atualizar destinação |
| DELETE | `/servico/material/{id}` | Excluir destinação |

### Relatórios

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/relatorios/obra/{idObra}?formato=xlsx` | Exportar planilha Excel da obra |
| GET | `/relatorios/movimentacao/estoque?formato=json\|csv` | Listar ou exportar movimentações |

### Perfis e Permissões

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/perfis` | Listar perfis |
| GET | `/perfis/{id}` | Buscar perfil por ID |
| GET | `/permissoes` | Listar permissões |
| GET | `/permissoes/{id}` | Buscar permissão por ID |
| GET | `/perfil-permissoes` | Listar vínculos perfil-permissão |

### Usuários

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/usuarios/operadores` | Listar operários |
| POST | `/usuarios/operadores` | Criar operário |

---

## Relatórios e Exportação

### Planilha Excel da Obra

`GET /relatorios/obra/{idObra}?formato=xlsx`

Gera um arquivo `.xlsx` com 5 abas:

1. **Resumo** — dados gerais da obra (código, nome, status, responsável, endereço)
2. **Estoque** — materiais em estoque com quantidades atual e mínima
3. **Movimentações** — histórico completo de entradas, saídas e ajustes
4. **Serviços** — lista de serviços com status e descrição
5. **Materiais nos serviços** — materiais destinados a cada serviço

Requer permissão: `RELATORIO_OBRA_EXPORTAR` ou `OBRA_LISTAR`.

### CSV de Movimentações

`GET /relatorios/movimentacao/estoque?formato=csv&idObra={id}&dataInicio={yyyy-MM-dd}&dataFim={yyyy-MM-dd}`

Exporta movimentações filtradas por obra e/ou período. O arquivo usa separador `;` e encoding UTF-8 com BOM (compatível com Excel).

Requer permissão: `RELATORIO_MOVIMENTACAO_LISTAR` ou `MOVIMENTACAO_ESTOQUE_LISTAR`.

---

## Comando de Voz para Estoque

`POST /obra/estoque/voz`

Endpoint projetado para integração com microsserviço de transcrição de voz (ex.: Python + Whisper). Recebe o resultado já extraído e atualiza o estoque automaticamente.

**Body:**
```json
{
  "material": "cimento",
  "quantidade": 50,
  "fornecedor": "Construmax",
  "acao": "entrada",
  "idObra": 1
}
```

**Lógica de processamento:**
- Busca o material por similaridade de nome (com remoção de acentos e tolerância a plural)
- Filtra por fornecedor se informado
- Detecta intenção de **entrada** ou **saída** analisando o campo `acao` (raízes em PT-BR como "subtrair", "retirar", "consumo", etc.)
- Cria ou consolida o estoque e registra movimentação automaticamente
- Lança exceção descritiva se o material não estiver cadastrado ou o estoque for insuficiente

---

## Documentação Interativa (Swagger)

Disponível em: [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)

Para autenticar no Swagger UI:
1. Execute `POST /auth/login` com as credenciais
2. Copie o valor do campo `token` da resposta
3. Clique em **Authorize** e cole o token (sem o prefixo "Bearer")

---

## Variáveis de Ambiente

As configurações estão em `src/main/resources/application.properties`. Para ambientes de produção, utilize variáveis de ambiente ou um arquivo `application-local.properties` (já no `.gitignore`).

| Propriedade | Padrão | Descrição |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5438/gestaoobraapi` | URL do banco de dados |
| `spring.datasource.username` | `postgres` | Usuário do banco |
| `spring.datasource.password` | `postgres` | Senha do banco |
| `jwt.secret` | `593a23e2-...` | Chave secreta para assinar o JWT |
| `jwt.expiration` | `86400000` | Expiração do token em ms (24h) |
| `server.port` | `8080` | Porta da aplicação |

> **Atenção:** Para produção, substitua o `jwt.secret` por uma string longa e aleatória e não versione credenciais no repositório.

---

## Licença

Projeto desenvolvido para fins de gestão de obras. Consulte a equipe responsável para informações sobre licenciamento.
