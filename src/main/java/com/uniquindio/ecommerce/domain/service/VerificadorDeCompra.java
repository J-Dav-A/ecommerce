package com.uniquindio.ecommerce.domain.service;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;

public class VerificadorDeCompra {

    private final CompraRepository compraRepository;

    public VerificadorDeCompra(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public void verificarQuePuedeComentar(String compradorId, String ofertaId) {
        boolean tieneCompraCompletada = compraRepository.existeCompraCompletada(compradorId, ofertaId);
        if (!tieneCompraCompletada) {
            throw new ReglaDominioException("Solo puede comentar quien haya completado la compra de esta oferta");
        }
    }
}