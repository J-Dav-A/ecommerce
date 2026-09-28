package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;

import java.util.List;

public class BuscarOfertas {

    private final OfertaRepository ofertaRepository;

    public BuscarOfertas(OfertaRepository ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    public List<Oferta> ejecutar() {
        return ofertaRepository.obtenerPublicadas();
    }
}