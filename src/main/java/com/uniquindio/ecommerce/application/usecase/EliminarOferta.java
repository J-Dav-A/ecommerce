package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarOferta {

    private final OfertaRepository ofertaRepository;

    @Transactional
    public void ejecutar(String ofertaId) {
        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId).orElseThrow();
        oferta.eliminar();
        ofertaRepository.guardar(oferta);
    }
}