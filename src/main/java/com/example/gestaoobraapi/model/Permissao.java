package com.example.gestaoobraapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissao", schema = "seguranca")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permissao extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao")
    private String descricao;
}
