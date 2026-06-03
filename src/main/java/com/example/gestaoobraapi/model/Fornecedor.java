package com.example.gestaoobraapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fornecedor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fornecedor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fornecedor_obra",
        joinColumns = @JoinColumn(name = "id_fornecedor"),
        inverseJoinColumns = @JoinColumn(name = "id_obra")
    )
    private List<Obra> obras = new ArrayList<>();
}
