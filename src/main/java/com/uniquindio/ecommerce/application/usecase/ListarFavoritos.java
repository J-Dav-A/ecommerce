package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Favorito;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarFavoritos {

    private final FavoritoRepository favoritoRepository;
    private final OfertaRepository ofertaRepository;

    // Las ofertas favoritas del comprador, la guardada más recientemente primero.
    // Si el vendedor eliminó una oferta, deja de aparecer (el favorito queda guardado pero oculto).
    @Transactional(readOnly = true)
    public List<Oferta> ejecutar(String compradorId) {
        return favoritoRepository.obtenerPorCompradorId(compradorId).stream()
                .map(Favorito::getOfertaId)
                .map(ofertaRepository::obtenerPorId)
                .flatMap(java.util.Optional::stream)
                .filter(o -> o.getEstado() != EstadoOferta.ELIMINADA)
                .toList();
    }
}