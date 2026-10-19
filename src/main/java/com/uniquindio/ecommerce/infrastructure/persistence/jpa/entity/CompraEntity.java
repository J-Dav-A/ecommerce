package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
@Getter @Setter
@NoArgsConstructor
public class CompraEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "comprador_id", nullable = false, length = 50)
    private String compradorId;

    @Column(name = "oferta_id", nullable = false, length = 50)
    private String ofertaId;

    @Embedded
    private PrecioEmbeddable precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCompra estado;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;
}