package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitarReembolso {

    private final CompraRepository repository;

    @Transactional
    public void ejecutar(String compraId, String compradorId, String motivo) {
        // Una compra ajena se trata como inexistente: así no se revela que existe
        Compra compra = repository.obtenerPorId(compraId)
                .filter(c -> c.perteneceA(compradorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no existe"));
        compra.solicitarReembolso(motivo);
        repository.guardar(compra);
    }
}