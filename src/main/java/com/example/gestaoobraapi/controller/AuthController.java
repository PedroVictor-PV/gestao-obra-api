package com.example.gestaoobraapi.controller;

import com.example.gestaoobraapi.api.AuthApi;
import com.example.gestaoobraapi.config.jwt.JwtUtil;
import com.example.gestaoobraapi.dto.LoginResponse;
import com.example.gestaoobraapi.dto.UserRequest;
import com.example.gestaoobraapi.dto.UsuarioResponse;
import com.example.gestaoobraapi.mapper.UsuarioMapper;
import com.example.gestaoobraapi.model.Usuario;
import com.example.gestaoobraapi.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioService usuarioService, UsuarioMapper usuarioMapper, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<UsuarioResponse> register(UserRequest request) {
        Usuario usuario = usuarioMapper.toModel(request);
        Usuario usuarioSalvo = usuarioService.registrar(usuario);
        UsuarioResponse response = usuarioMapper.toResponse(usuarioSalvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<LoginResponse> login(UserRequest request) {
        Usuario usuario = usuarioMapper.toModel(request);
        Usuario usuarioLogado = usuarioService.login(usuario);

        String token = jwtUtil.generateToken(usuarioLogado.getChave());

        LoginResponse response = new LoginResponse();
        response.setToken(token);

        return ResponseEntity.ok(response);
    }
}
