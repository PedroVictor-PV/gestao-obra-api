package com.example.gestaoobraapi.service;

import com.example.gestaoobraapi.exception.ChaveAlreadyExistsException;
import com.example.gestaoobraapi.exception.InvalidCredentialsException;
import com.example.gestaoobraapi.model.Usuario;
import com.example.gestaoobraapi.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.findByChave(usuario.getChave()).isPresent()) {
            throw new ChaveAlreadyExistsException("A chave '" + usuario.getChave() + "' já está em uso.");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario login(Usuario usuario) {
        Usuario usuarioEncontrado = usuarioRepository.findByChave(usuario.getChave())
                .orElseThrow(() -> new InvalidCredentialsException("Usuário não encontrado ou senha incorreta."));

        if (!passwordEncoder.matches(usuario.getSenha(), usuarioEncontrado.getSenha())) {
            throw new InvalidCredentialsException("Usuário não encontrado ou senha incorreta.");
        }

        return usuarioEncontrado;
    }
}
