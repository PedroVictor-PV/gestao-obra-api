package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.PerfilPermissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfilPermissaoRepository extends JpaRepository<PerfilPermissao, Long> {

    @Query("SELECT pp FROM PerfilPermissao pp JOIN FETCH pp.perfil JOIN FETCH pp.permissao")
    List<PerfilPermissao> findAllWithPerfilAndPermissao();
}
