package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import com.uniquindio.ecommerce.domain.valueobject.EstadoClaveDigital;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "claves_digitales")
@Getter @Setter
@NoArgsConstructor
public class ClaveDigitalEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "oferta_id", nullable = false, length = 50)
    private String ofertaId;

    @Column(nullable = false, length = 60)
    private String codigo;

    @Column(name = "max_activaciones", nullable = false)
    private int maxActivaciones;

    @Column(nullable = false)
    private boolean transferible;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoClaveDigital estado;

    @Column(name = "comprador_id", length = 50)
    private String compradorId;

    @Column(name = "fecha_asignacion")
    private Instant fechaAsignacion;
}