package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.PoliticaReembolso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EliminarOferta {

    private final OfertaRepository ofertaRepository;
    private final CompraRepository compraRepository;

    @Transactional
    public void ejecutar(String ofertaId, String vendedorId) {
        // Una oferta ajena se trata como inexistente
        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId)
                .filter(o -> o.perteneceA(vendedorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La oferta no existe"));

        LocalDateTime desde = LocalDateTime.now().minus(PoliticaReembolso.PLAZO);
        if (compraRepository.existenComprasRecientes(ofertaId, desde)) {
            throw new ReglaDominioException("No se puede eliminar la oferta: tiene compras recientes sujetas a reembolso");
        }

        oferta.eliminar();
        ofertaRepository.guardar(oferta);
    }
}