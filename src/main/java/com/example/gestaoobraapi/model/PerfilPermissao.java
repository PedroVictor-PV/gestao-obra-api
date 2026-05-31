package com.example.gestaoobraapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "perfil_permissao", schema = "seguranca")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilPermissao extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_permissao", nullable = false)
    private Permissao permissao;
}
