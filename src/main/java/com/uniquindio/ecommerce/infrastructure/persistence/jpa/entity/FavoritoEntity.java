
package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "favoritos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_favorito_comprador_oferta",
                        columnNames = {"comprador_id", "oferta_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class FavoritoEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "comprador_id", nullable = false, length = 50)
    private String compradorId;

    @Column(name = "oferta_id", nullable = false, length = 50)
    private String ofertaId;
}
