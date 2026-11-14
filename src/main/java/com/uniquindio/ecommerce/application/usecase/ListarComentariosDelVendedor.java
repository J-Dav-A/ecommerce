package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

// Comentarios recibidos en las ofertas del vendedor (sirve de "bandeja" de comentarios nuevos)
@Service
@RequiredArgsConstructor
public class ListarComentariosDelVendedor {

    private final OfertaRepository ofertaRepository;
    private final ComentarioRepository comentarioRepository;

    @Transactional(readOnly = true)
    public List<Comentario> ejecutar(String vendedorId, boolean soloSinResponder) {
        return ofertaRepository.obtenerPorVendedorId(vendedorId).stream()
                .map(Oferta::getId)
                .flatMap(ofertaId -> comentarioRepository.buscarPorOfertaId(ofertaId).stream())
                .filter(c -> !soloSinResponder || !c.estaRespondido())
                .sorted(Comparator.comparing(Comentario::getFecha).reversed())
                .toList();
    }
}