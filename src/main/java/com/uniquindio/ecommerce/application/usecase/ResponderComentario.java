package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResponderComentario {

    private final ComentarioRepository comentarioRepository;
    private final OfertaRepository ofertaRepository;

    @Transactional
    public Comentario ejecutar(String comentarioId, String vendedorId, String respuesta) {
        Comentario comentario = comentarioRepository.obtenerPorId(comentarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El comentario no existe"));

        // Solo el vendedor dueño de la oferta responde; para los demás el comentario "no existe"
        ofertaRepository.obtenerPorId(comentario.getOfertaId())
                .filter(o -> o.perteneceA(vendedorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("El comentario no existe"));

        comentario.responder(respuesta, LocalDateTime.now());
        comentarioRepository.guardar(comentario);
        return comentario;
    }
}