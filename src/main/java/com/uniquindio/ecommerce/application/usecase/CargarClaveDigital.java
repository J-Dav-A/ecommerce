package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.LicenciaDigital;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CargarClaveDigital {

    private final OfertaRepository ofertaRepository;
    private final ClaveDigitalRepository claveDigitalRepository;

    @Transactional
    public ClaveDigital ejecutar(String ofertaId, String vendedorId, String codigo,
                                 int maxActivaciones, boolean transferible) {
        // Solo el vendedor dueño de la oferta puede cargarle claves
        ofertaRepository.obtenerPorId(ofertaId)
                .filter(o -> o.perteneceA(vendedorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La oferta no existe"));

        ClaveDigital clave = ClaveDigital.crear(UUID.randomUUID().toString(), ofertaId, codigo,
                new LicenciaDigital(maxActivaciones, transferible));
        claveDigitalRepository.guardar(clave);
        return clave;
    }
}