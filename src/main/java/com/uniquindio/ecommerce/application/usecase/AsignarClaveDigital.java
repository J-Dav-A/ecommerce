package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;

import java.time.Instant;
import java.util.UUID;

/**
 Caso de uso AsignarClaveDigital
 Recibe la intención de asignar una clave digital a un comprador la busca en el repositorio invoca el comportamiento del dominio y guarda el resultado
 No contiene ninguna regla de negocio propia.
 */
public class AsignarClaveDigital {

    private final ClaveDigitalRepository repository;

    public AsignarClaveDigital(ClaveDigitalRepository repository) {
        this.repository = repository;
    }

    public ClaveDigital ejecutar(UUID claveId, UUID compradorId) {
        ClaveDigital clave = repository.obtenerPorId(claveId)
                .orElseThrow(() -> new ReglaDominioException("La clave digital no existe."));

        clave.asignar(compradorId, Instant.now());
        repository.guardar(clave);

        return clave;
    }
}
