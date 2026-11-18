package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuitarFavorito {

    private final FavoritoRepository favoritoRepository;

    @Transactional
    public void ejecutar(String compradorId, String ofertaId) {
        if (!favoritoRepository.eliminar(compradorId, ofertaId)) {
            throw new RecursoNoEncontradoException("La oferta no está en tus favoritos");
        }
    }
}