package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.UsuarioObra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioObraRepository extends JpaRepository<UsuarioObra, Long> {

    @Query("SELECT uo.obra.id FROM UsuarioObra uo WHERE uo.usuario.id = :usuarioId AND uo.ativo = true")
    List<Long> findObraIdsByUsuarioId(@Param("usuarioId") Long usuarioId);

    java.util.Optional<UsuarioObra> findByUsuarioIdAndObraId(Long usuarioId, Long obraId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM UsuarioObra uo WHERE uo.usuario.id = :usuarioId AND uo.obra.id = :obraId")
    void deleteByUsuarioIdAndObraId(@Param("usuarioId") Long usuarioId, @Param("obraId") Long obraId);

    @Query("SELECT uo.usuario FROM UsuarioObra uo WHERE uo.obra.id = :obraId AND uo.ativo = true")
    List<com.example.gestaoobraapi.model.Usuario> findUsuariosByObraId(@Param("obraId") Long obraId);
}
