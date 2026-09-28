package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;

import java.util.List;

public class ConsultarHistorialDeCompras {

    private final CompraRepository compraRepository;

    public ConsultarHistorialDeCompras(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public List<Compra> ejecutar(String compradorId) {

        if (compradorId == null || compradorId.isBlank()) {
            throw new ReglaDominioException("Debe indicarse el comprador.");
        }

        return compraRepository.obtenerPorCompradorId(compradorId);
    }
}