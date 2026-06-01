package com.example.gestaoobraapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria_material")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaMaterial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
}
