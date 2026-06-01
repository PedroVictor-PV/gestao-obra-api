import json
from pathlib import Path

p = Path("src/main/resources/swagger/gestaoobrasapi.json")
d = json.loads(p.read_text())
defaults = {"201": "Criado", "200": "OK", "204": "Excluído"}
for path, methods in d["paths"].items():
    if not path.startswith("/servico"):
        continue
    for op in methods.values():
        if not isinstance(op, dict):
            continue
        for code, resp in op.get("responses", {}).items():
            resp.setdefault("description", defaults.get(code, "Resposta"))
p.write_text(json.dumps(d, indent=2) + "\n")
