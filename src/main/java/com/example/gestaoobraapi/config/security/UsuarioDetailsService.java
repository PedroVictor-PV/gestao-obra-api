package com.example.gestaoobraapi.config.security;

import com.example.gestaoobraapi.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByChave(username)
                .map(usuario -> User.builder()
                        .username(usuario.getChave())
                        .password(usuario.getSenha())
                        .authorities("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com a chave: " + username));
    }
}
