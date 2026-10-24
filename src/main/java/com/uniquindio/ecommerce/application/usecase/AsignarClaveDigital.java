package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AsignarClaveDigital {

    private final ClaveDigitalRepository repository;

    @Transactional
    public ClaveDigital ejecutar(String claveId, String compradorId) {
        ClaveDigital clave = repository.obtenerPorId(claveId).orElseThrow();
        clave.asignar(compradorId, Instant.now());
        repository.guardar(clave);
        return clave;
    }
}