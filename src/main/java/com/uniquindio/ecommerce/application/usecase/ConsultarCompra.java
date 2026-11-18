package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultarCompra {

    private final CompraRepository compraRepository;

    public Compra ejecutar(String compraId, String compradorId) {
        return compraRepository.obtenerPorId(compraId)
                .filter(c -> c.perteneceA(compradorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no existe"));
    }
}