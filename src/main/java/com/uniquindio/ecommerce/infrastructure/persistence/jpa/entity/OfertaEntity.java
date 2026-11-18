package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ofertas")
@Getter @Setter
@NoArgsConstructor
public class OfertaEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "vendedor_id", nullable = false, length = 50)
    private String vendedorId;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Embedded
    private PrecioEmbeddable precio;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Plataforma plataforma;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOferta estado;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oferta_imagenes", joinColumns = @JoinColumn(name = "oferta_id"))
    @OrderColumn(name = "orden")
    @Column(name = "url", length = 500)
    private List<String> imagenes = new ArrayList<>();
}