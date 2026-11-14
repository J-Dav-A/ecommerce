package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EntregarClaveDigital {

    private final CompraRepository compraRepository;
    private final ClaveDigitalRepository claveDigitalRepository;

    @Transactional
    public ClaveDigital ejecutar(String compraId, String compradorId) {
        Compra compra = compraRepository.obtenerPorId(compraId)
                .filter(c -> c.perteneceA(compradorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no existe"));
        compra.verificarQuePuedeRecibirLaClave();

        ClaveDigital clave = claveDigitalRepository.obtenerDeComprador(compra.getOfertaId(), compradorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La clave digital no existe"));
        clave.entregar();
        claveDigitalRepository.guardar(clave);
        return clave;
    }
}