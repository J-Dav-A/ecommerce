package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// La restricción única evita duplicados incluso si dos peticiones llegan a la vez
@Entity
@Table(name = "favoritos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"comprador_id", "oferta_id"}))
@Getter @Setter
@NoArgsConstructor
public class FavoritoEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "comprador_id", nullable = false, length = 50)
    private String compradorId;

    @Column(name = "oferta_id", nullable = false, length = 50)
    private String ofertaId;

    @Column(nullable = false)
    private LocalDateTime fecha;
}