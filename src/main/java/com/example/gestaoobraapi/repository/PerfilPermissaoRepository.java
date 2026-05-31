package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.PerfilPermissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilPermissaoRepository extends JpaRepository<PerfilPermissao, Long> {
}
