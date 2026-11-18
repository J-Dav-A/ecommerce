package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// El id es el del usuario: solo hay un código vigente por usuario
@Entity
@Table(name = "codigos_recuperacion")
@Getter @Setter
@NoArgsConstructor
public class CodigoRecuperacionEntity {

    @Id
    @Column(name = "usuario_id", length = 50)
    private String usuarioId;

    @Column(name = "codigo_hash", nullable = false, length = 100)
    private String codigoHash;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(nullable = false)
    private int intentos;
}