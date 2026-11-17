
package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgregarFavorito {

    private final FavoritoRepository favoritoRepository;
    private final OfertaRepository ofertaRepository;

    @Transactional
    public Oferta ejecutar(String compradorId, String ofertaId) {

        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId)
                .orElseThrow(() -> new ReglaDominioException("La oferta no existe"));

        if (oferta.getEstado() != EstadoOferta.PUBLICADA) {
            throw new ReglaDominioException("Solo puedes agregar ofertas publicadas a favoritos");
        }

        if (favoritoRepository.obtenerPorCompradorYOferta(compradorId, ofertaId).isEmpty()) {
            Favorito favorito = Favorito.crear(
                    UUID.randomUUID().toString(),
                    compradorId,
                    ofertaId
            );

            favoritoRepository.guardar(favorito);
        }

        return oferta;
    }
}
