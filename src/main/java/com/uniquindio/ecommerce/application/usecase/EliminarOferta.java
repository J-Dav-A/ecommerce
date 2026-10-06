package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;

import java.util.UUID;

/**
 * Caso de uso: EliminarOferta.
 *
 * Permite a un vendedor eliminar una de sus ofertas del catálogo.
 * Siempre se trata de una eliminación lógica: la oferta nunca se
 * borra físicamente, para conservar el historial de ventas,
 * reembolsos y auditoría (Regla 5).
 */
public class EliminarOferta {

    private final OfertaRepository ofertaRepository;

    public EliminarOferta(OfertaRepository ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    public void ejecutar(UUID ofertaId) {

        if (ofertaId == null) {
            throw new ReglaDominioException("Debe indicarse la oferta que se desea eliminar");
        }

        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId)
                .orElseThrow(() -> new ReglaDominioException("La oferta no existe"));

        oferta.eliminar();

        ofertaRepository.guardar(oferta);
    }
}