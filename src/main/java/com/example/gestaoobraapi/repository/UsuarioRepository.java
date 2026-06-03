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

    @Query(value = """
            SELECT p.codigo
            FROM seguranca.usuario u
            INNER JOIN seguranca.usuario_perfil up ON up.id_usuario = u.id
            INNER JOIN seguranca.perfil p ON p.id = up.id_perfil
            WHERE u.id = :usuarioId
              AND u.ativo = true
              AND up.ativo = true
              AND p.ativo = true
            LIMIT 1
            """, nativeQuery = true)
    String findPerfilCodigoByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query(value = """
            SELECT u.*
            FROM seguranca.usuario u
            INNER JOIN seguranca.usuario_perfil up ON up.id_usuario = u.id
            INNER JOIN seguranca.perfil p ON p.id = up.id_perfil
            WHERE p.codigo = 'MESTRE_OBRAS'
              AND u.ativo = true
              AND up.ativo = true
              AND p.ativo = true
            """, nativeQuery = true)
    List<Usuario> findMestresDeObras();
    @Query(value = """
            SELECT u.*
            FROM seguranca.usuario u
            INNER JOIN seguranca.usuario_perfil up ON up.id_usuario = u.id
            INNER JOIN seguranca.perfil p ON p.id = up.id_perfil
            WHERE p.codigo = 'OPERARIO'
              AND u.ativo = true
              AND up.ativo = true
              AND p.ativo = true
            """, nativeQuery = true)
    List<Usuario> findOperarios();

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = """
            INSERT INTO seguranca.usuario_perfil (id_usuario, id_perfil, ativo, criado_por, criado_em)
            VALUES (:usuarioId, (SELECT id FROM seguranca.perfil WHERE codigo = :perfilCodigo LIMIT 1), true, 1, NOW())
            ON CONFLICT (id_usuario, id_perfil) DO NOTHING
            """, nativeQuery = true)
    void adicionarPerfil(@Param("usuarioId") Long usuarioId, @Param("perfilCodigo") String perfilCodigo);

    @Query(value = """
            SELECT u.*
            FROM seguranca.usuario u
            INNER JOIN seguranca.usuario_perfil up ON up.id_usuario = u.id
            INNER JOIN seguranca.perfil p ON p.id = up.id_perfil
            WHERE p.codigo = 'ADMIN'
              AND u.ativo = true
              AND up.ativo = true
              AND p.ativo = true
            """, nativeQuery = true)
    List<Usuario> findAdministradores();
}

