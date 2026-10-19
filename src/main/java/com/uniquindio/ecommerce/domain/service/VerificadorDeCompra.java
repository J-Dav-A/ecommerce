package com.uniquindio.ecommerce.domain.service;

import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerificadorDeCompra {

    private final CompraRepository compraRepository;

    public boolean haCompradoOferta(String compradorId, String ofertaId) {
        return compraRepository.existeCompraCompletada(compradorId, ofertaId);
    }
}