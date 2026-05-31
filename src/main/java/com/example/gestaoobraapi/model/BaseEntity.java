package com.example.gestaoobraapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_por")
    private Long criadoPor;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "alterado_por")
    private Long alteradoPor;

    @Column(name = "alterado_em", nullable = false)
    private LocalDateTime alteradoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.alteradoEm = LocalDateTime.now();
        if (this.ativo == null) {
            this.ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.alteradoEm = LocalDateTime.now();
    }
}
