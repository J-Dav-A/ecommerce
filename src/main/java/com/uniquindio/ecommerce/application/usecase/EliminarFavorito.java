
package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarFavorito {

    private final FavoritoRepository favoritoRepository;

    @Transactional
    public void ejecutar(String compradorId, String ofertaId) {
        favoritoRepository.eliminar(compradorId, ofertaId);
    }
}
