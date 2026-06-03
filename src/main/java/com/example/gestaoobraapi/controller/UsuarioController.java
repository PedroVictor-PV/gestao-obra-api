package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.model.Usuario;
import com.example.gestaoobraapi.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/operadores")
    public ResponseEntity<List<UsuarioDto>> listarOperarios() {
        List<UsuarioDto> operarios = usuarioService.listarOperarios().stream()
                .map(u -> new UsuarioDto(u.getId(), u.getNome(), u.getChave()))
                .toList();
        return ResponseEntity.ok(operarios);
    }

    @PostMapping("/operadores")
    public ResponseEntity<UsuarioDto> criarOperario(@RequestBody CriarUsuarioDto dto) {
        Usuario novo = new Usuario();
        novo.setNome(dto.getNome());
        novo.setChave(dto.getChave());
        novo.setSenha(dto.getSenha());
        novo.setAtivo(true);
        
        Usuario salvo = usuarioService.criarOperario(novo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UsuarioDto(salvo.getId(), salvo.getNome(), salvo.getChave()));
    }

    public static class UsuarioDto {
        private Long id;
        private String nome;
        private String chave;

        public UsuarioDto(Long id, String nome, String chave) {
            this.id = id;
            this.nome = nome;
            this.chave = chave;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getChave() { return chave; }
        public void setChave(String chave) { this.chave = chave; }
    }

    public static class CriarUsuarioDto {
        private String nome;
        private String chave;
        private String senha;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getChave() { return chave; }
        public void setChave(String chave) { this.chave = chave; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
}
