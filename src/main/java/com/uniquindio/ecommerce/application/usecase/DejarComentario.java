package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DejarComentario {

    private final ComentarioRepository comentarioRepository;
    private final CompraRepository compraRepository;

    @Transactional
    public Comentario ejecutar(String compradorId, String ofertaId, String texto, int calificacion) {
        // Solo comenta quien tiene una compra completada de esa oferta
        Compra compra = compraRepository.obtenerPorCompradorId(compradorId).stream()
                .filter(c -> c.getOfertaId().equals(ofertaId) && c.estaCompletada())
                .findFirst()
                .orElseThrow(() -> new ReglaDominioException(
                        "El usuario debe haber comprado la oferta para dejar un comentario."));

        // Máximo un comentario por compra
        if (comentarioRepository.existePorCompraId(compra.getId())) {
            throw new ReglaDominioException("Ya comentaste esta compra");
        }

        Comentario comentario = Comentario.crear(UUID.randomUUID().toString(), compradorId, ofertaId,
                compra.getId(), texto, calificacion, LocalDateTime.now());
        comentarioRepository.guardar(comentario);
        return comentario;
    }
}