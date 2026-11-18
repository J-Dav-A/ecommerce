package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgregarFavorito {

    private final FavoritoRepository favoritoRepository;
    private final OfertaRepository ofertaRepository;

    // Solo se guardan ofertas publicadas; una oferta en borrador o eliminada se trata como inexistente
    @Transactional
    public Oferta ejecutar(String compradorId, String ofertaId) {
        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId)
                .filter(o -> o.getEstado() == EstadoOferta.PUBLICADA)
                .orElseThrow(() -> new RecursoNoEncontradoException("La oferta no existe"));

        if (favoritoRepository.existe(compradorId, ofertaId)) {
            throw new ReglaDominioException("La oferta ya está en tus favoritos");
        }

        favoritoRepository.guardar(Favorito.crear(UUID.randomUUID().toString(), compradorId, ofertaId, LocalDateTime.now()));
        return oferta;
    }
}