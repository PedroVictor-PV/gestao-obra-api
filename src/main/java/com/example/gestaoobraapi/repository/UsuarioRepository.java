package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByChave(String chave);

    @Query(value = """
            SELECT DISTINCT p.codigo
            FROM seguranca.usuario u
            INNER JOIN seguranca.usuario_perfil up ON up.id_usuario = u.id
            INNER JOIN seguranca.perfil_permissao pp ON pp.id_perfil = up.id_perfil
            INNER JOIN seguranca.permissao p ON p.id = pp.id_permissao
            WHERE u.chave = :chave
              AND u.ativo = true
              AND up.ativo = true
              AND pp.ativo = true
              AND p.ativo = true
            """, nativeQuery = true)
    List<String> findCodigosPermissaoByChave(@Param("chave") String chave);
}
