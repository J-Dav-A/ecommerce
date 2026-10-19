package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarOfertas {

    private final OfertaRepository ofertaRepository;

    @Transactional(readOnly = true)
    public List<Oferta> ejecutar() {
        return ofertaRepository.obtenerPublicadas();
    }
}