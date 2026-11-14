package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.PoliticaReembolso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SolicitarReembolso {

    private final CompraRepository repository;
    private final ClaveDigitalRepository claveDigitalRepository;

    @Transactional
    public void ejecutar(String compraId, String compradorId, String motivo) {
        // Una compra ajena se trata como inexistente: así no se revela que existe
        Compra compra = repository.obtenerPorId(compraId)
                .filter(c -> c.perteneceA(compradorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no existe"));

        compra.solicitarReembolso(motivo);

        // La clave debe estar dentro del plazo y sin canjear; si no, la excepción cancela todo
        claveDigitalRepository.obtenerDeComprador(compra.getOfertaId(), compradorId).ifPresent(clave -> {
            clave.reembolsar(Instant.now(), PoliticaReembolso.PLAZO);
            claveDigitalRepository.guardar(clave);
        });

        repository.guardar(compra);
    }
}