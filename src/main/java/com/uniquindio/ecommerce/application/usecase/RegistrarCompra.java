package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;

import java.util.UUID;

/**
 * Caso de uso: RegistrarCompra.
 *
 * Permite a un comprador adquirir una Oferta publicada
 * dentro del marketplace.
 */
public class RegistrarCompra {

    private final OfertaRepository ofertaRepository;
    private final CompraRepository compraRepository;

    public RegistrarCompra(
            OfertaRepository ofertaRepository,
            CompraRepository compraRepository
    ) {
        this.ofertaRepository = ofertaRepository;
        this.compraRepository = compraRepository;
    }

    /**
     * Ejecuta el proceso de registrar una compra.
     *
     * @param ofertaId identificador de la oferta que se desea comprar.
     * @param compradorId identificador del comprador.
     * @return la compra registrada.
     */
    public Compra ejecutar(UUID ofertaId, UUID compradorId) {

        if (ofertaId == null) {
            throw new ReglaDominioException(
                    "Debe indicarse la oferta que se desea comprar"
            );
        }

        if (compradorId == null) {
            throw new ReglaDominioException(
                    "Debe indicarse el comprador"
            );
        }

        // La oferta debe existir.
        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId)
                .orElseThrow(() ->
                        new ReglaDominioException(
                                "La oferta no existe"
                        )
                );

        // Una oferta debe estar publicada para poder ser comprada.
        if (oferta.getEstado() != EstadoOferta.PUBLICADA) {
            throw new ReglaDominioException(
                    "La oferta no está disponible para compra"
            );
        }

        String ofertaIdString = oferta.getId().toString();
        String compradorIdString = compradorId.toString();

        /*
         * Regla 5:
         * No se puede confirmar una compra si el comprador
         * ya tiene una compra activa del mismo producto.
         */
        if (compraRepository.existeCompraActiva(
                compradorIdString,
                ofertaIdString
        )) {
            throw new ReglaDominioException(
                    "El comprador ya tiene una compra activa de esta oferta"
            );
        }

        // La oferta debe tener stock disponible.
        if (oferta.getStock() <= 0) {
            throw new ReglaDominioException(
                    "La oferta no tiene stock disponible"
            );
        }

        /*
         * La compra conserva el precio de la oferta
         * en el momento en que se realiza.
         */
        Compra compra = Compra.realizar(
                UUID.randomUUID().toString(),
                ofertaIdString,
                compradorIdString,
                oferta.getPrecio()
        );

        // Se confirma la compra.
        compra.confirmar();

        // Se descuenta una unidad del stock de la oferta.
        oferta.descontarStock(1);

        // Se persisten los cambios.
        compraRepository.guardar(compra);
        ofertaRepository.guardar(oferta);

        return compra;
    }
}