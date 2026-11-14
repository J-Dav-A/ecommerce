package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
@Getter @Setter
@NoArgsConstructor
public class ComentarioEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "comprador_id", nullable = false, length = 50)
    private String compradorId;

    @Column(name = "oferta_id", nullable = false, length = 50)
    private String ofertaId;

    @Column(name = "compra_id", nullable = false, length = 50)
    private String compraId;

    @Column(nullable = false, length = 500)
    private String texto;

    @Column(nullable = false)
    private int calificacion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 500)
    private String respuesta;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;
}