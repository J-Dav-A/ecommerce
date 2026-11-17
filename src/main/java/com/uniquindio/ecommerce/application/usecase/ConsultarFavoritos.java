
package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.FavoritoRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarFavoritos {

    private final FavoritoRepository favoritoRepository;
    private final OfertaRepository ofertaRepository;

    @Transactional(readOnly = true)
    public List<Oferta> ejecutar(String compradorId) {

        return favoritoRepository.buscarOfertaIdsPorCompradorId(compradorId)
                .stream()
                .map(ofertaRepository::obtenerPorId)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .toList();
    }
}
