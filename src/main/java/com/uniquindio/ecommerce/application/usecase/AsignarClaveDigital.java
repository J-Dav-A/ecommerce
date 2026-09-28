package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;

import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso: AsignarClaveDigital.
 *
 * Permite asignar una clave digital disponible a un comprador.
 *
 * El caso de uso coordina la obtención y persistencia de la clave,
 * mientras que las reglas propias de la clave permanecen dentro
 * de la entidad ClaveDigital.
 */
public class AsignarClaveDigital {

    private final ClaveDigitalRepository repository;

    public AsignarClaveDigital(ClaveDigitalRepository repository) {
        this.repository = repository;
    }

    /**
     * Ejecuta el proceso de asignación de una clave digital.
     *
     * @param claveId identificador de la clave digital.
     * @param compradorId identificador del comprador.
     * @return clave digital asignada.
     */
    public ClaveDigital ejecutar(UUID claveId, UUID compradorId) {

        // Validación: debe indicarse la clave que se desea asignar.
        if (claveId == null) {
            throw new ReglaDominioException(
                    "Debe indicarse la clave digital"
            );
        }

        // Validación: debe indicarse el comprador que recibirá la clave.
        if (compradorId == null) {
            throw new ReglaDominioException(
                    "Debe indicarse el comprador"
            );
        }

        // La clave debe existir para poder asignarla.
        ClaveDigital clave = repository.obtenerPorId(claveId)
                .orElseThrow(() ->
                        new ReglaDominioException(
                                "La clave digital no existe."
                        )
                );

        /*
         * Regla 2:
         * Una clave digital solo puede asignarse si está disponible
         * y, una vez asignada, no puede volver a comercializarse.
         *
         * Esta regla se encuentra protegida por la entidad
         * ClaveDigital mediante el método asignar().
         */
        clave.asignar(compradorId, Instant.now());

        // Se persiste el nuevo estado de la clave.
        repository.guardar(clave);

        return clave;
    }
}