import json
from pathlib import Path

p = Path("src/main/resources/swagger/gestaoobrasapi.json")
d = json.loads(p.read_text())

def crud(base, tag, prefix, req, res, list_op):
    pid = f"{base}/{{id}}"
    return {
        base: {
            "post": {"tags": [tag], "operationId": f"criar{prefix}", "security": [{"bearerAuth": []}],
                "requestBody": {"required": True, "content": {"application/json": {"schema": {"$ref": req}}}},
                "responses": {"201": {"description": "Criado", "content": {"application/json": {"schema": {"$ref": res}}}}}},
            "get": {"tags": [tag], "operationId": list_op, "security": [{"bearerAuth": []}],
                "responses": {"200": {"description": "OK", "content": {"application/json": {"schema": {"type": "array", "items": {"$ref": res}}}}}}}
        },
        pid: {
            "get": {"tags": [tag], "operationId": f"buscar{prefix}PorId", "security": [{"bearerAuth": []}],
                "parameters": [{"name": "id", "in": "path", "required": True, "schema": {"type": "integer", "format": "int64"}}],
                "responses": {"200": {"description": "OK", "content": {"application/json": {"schema": {"$ref": res}}}}}},
            "put": {"tags": [tag], "operationId": f"atualizar{prefix}", "security": [{"bearerAuth": []}],
                "parameters": [{"name": "id", "in": "path", "required": True, "schema": {"type": "integer", "format": "int64"}}],
                "requestBody": {"required": True, "content": {"application/json": {"schema": {"$ref": req}}}},
                "responses": {"200": {"description": "OK", "content": {"application/json": {"schema": {"$ref": res}}}}}},
            "delete": {"tags": [tag], "operationId": f"excluir{prefix}", "security": [{"bearerAuth": []}],
                "parameters": [{"name": "id", "in": "path", "required": True, "schema": {"type": "integer", "format": "int64"}}],
                "responses": {"204": {"description": "Excluído"}}}
        }
    }

for t in [{"name": "StatusServico"}, {"name": "Servicos"}, {"name": "ServicoMaterial"}]:
    if t not in d["tags"]:
        d["tags"].append(t)

d["paths"].update(crud("/servico/status", "StatusServico", "StatusServico", "#/components/schemas/StatusServicoRequest", "#/components/schemas/StatusServicoResponse", "listarStatusServico"))
d["paths"].update(crud("/servico", "Servicos", "Servico", "#/components/schemas/ServicoRequest", "#/components/schemas/ServicoResponse", "listarServicos"))
d["paths"].update(crud("/servico/material", "ServicoMaterial", "ServicoMaterial", "#/components/schemas/ServicoMaterialRequest", "#/components/schemas/ServicoMaterialResponse", "listarServicoMaterial"))

a = {"ativo": {"type": "boolean"}, "criadoPor": {"type": "integer", "format": "int64", "nullable": True},
     "criadoEm": {"type": "string", "format": "date-time"}, "alteradoPor": {"type": "integer", "format": "int64", "nullable": True},
     "alteradoEm": {"type": "string", "format": "date-time"}}
d["components"]["schemas"].update({
    "StatusServicoRequest": {"type": "object", "required": ["codigo", "nome"], "properties": {"codigo": {"type": "string"}, "nome": {"type": "string"}, "descricao": {"type": "string"}}},
    "StatusServicoResponse": {"type": "object", "properties": {"id": {"type": "integer", "format": "int64"}, "codigo": {"type": "string"}, "nome": {"type": "string"}, "descricao": {"type": "string"}, **a}},
    "ServicoRequest": {"type": "object", "required": ["idObra", "idStatusServico", "nome"], "properties": {"idObra": {"type": "integer", "format": "int64"}, "idStatusServico": {"type": "integer", "format": "int64"}, "nome": {"type": "string"}, "descricao": {"type": "string"}, "observacao": {"type": "string"}}},
    "ServicoResponse": {"type": "object", "properties": {"id": {"type": "integer", "format": "int64"}, "idObra": {"type": "integer", "format": "int64"}, "nomeObra": {"type": "string"}, "idStatusServico": {"type": "integer", "format": "int64"}, "nomeStatusServico": {"type": "string"}, "nome": {"type": "string"}, "descricao": {"type": "string"}, "observacao": {"type": "string"}, **a}},
    "ServicoMaterialRequest": {"type": "object", "required": ["idServico", "idObra", "idMaterial", "quantidade"], "properties": {"idServico": {"type": "integer", "format": "int64"}, "idObra": {"type": "integer", "format": "int64"}, "idMaterial": {"type": "integer", "format": "int64"}, "quantidade": {"type": "number"}, "observacao": {"type": "string"}}},
    "ServicoMaterialResponse": {"type": "object", "properties": {"id": {"type": "integer", "format": "int64"}, "idServico": {"type": "integer", "format": "int64"}, "nomeServico": {"type": "string"}, "idObra": {"type": "integer", "format": "int64"}, "nomeObra": {"type": "string"}, "idMaterial": {"type": "integer", "format": "int64"}, "nomeMaterial": {"type": "string"}, "quantidade": {"type": "number"}, "observacao": {"type": "string"}}}
})
p.write_text(json.dumps(d, indent=2) + "\n")
