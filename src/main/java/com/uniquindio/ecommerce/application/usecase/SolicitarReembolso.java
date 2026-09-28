package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso: SolicitarReembolso.
 *
 * Permite a un comprador solicitar el reembolso de una clave digital
 * asociada a una compra.
 *
 * El caso de uso coordina la obtención y persistencia de la clave,
 * mientras que la regla de negocio del reembolso permanece
 * dentro de la entidad ClaveDigital.
 */
public class SolicitarReembolso {

    private final ClaveDigitalRepository claveDigitalRepository;

    public SolicitarReembolso(
            ClaveDigitalRepository claveDigitalRepository
    ) {
        this.claveDigitalRepository = claveDigitalRepository;
    }

    /**
     * Ejecuta la solicitud de reembolso de una clave digital.
     *
     * @param claveDigitalId identificador de la clave digital.
     * @param ahora fecha y hora en la que se solicita el reembolso.
     * @param plazo plazo máximo permitido para solicitar el reembolso.
     */
    public void ejecutar(
            UUID claveDigitalId,
            Instant ahora,
            Duration plazo
    ) {

        // Validación: debe indicarse la clave que se desea reembolsar.
        if (claveDigitalId == null) {
            throw new ReglaDominioException(
                    "Debe indicarse la clave digital"
            );
        }

        // Validación: debe indicarse el momento de la solicitud.
        if (ahora == null) {
            throw new ReglaDominioException(
                    "Debe indicarse la fecha de la solicitud"
            );
        }

        // Validación: debe indicarse el plazo permitido.
        if (plazo == null) {
            throw new ReglaDominioException(
                    "Debe indicarse el plazo de reembolso"
            );
        }

        // La clave debe existir para poder solicitar el reembolso.
        ClaveDigital claveDigital = claveDigitalRepository
                .obtenerPorId(claveDigitalId)
                .orElseThrow(() ->
                        new ReglaDominioException(
                                "La clave digital no existe"
                        )
                );

        /*
         * Regla 3:
         * No se aceptan reembolsos de claves digitales ya canjeadas.
         * Solo procede el reembolso si la clave sigue sin canjear
         * y la solicitud se realiza dentro del plazo establecido.
         *
         * La validación de esta regla se encuentra en la entidad
         * ClaveDigital mediante el método reembolsar().
         */
        claveDigital.reembolsar(ahora, plazo);

        // Se persiste el nuevo estado de la clave.
        claveDigitalRepository.guardar(claveDigital);
    }
}