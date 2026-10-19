package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.service.VerificadorDeCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DejarComentario {

    private final ComentarioRepository comentarioRepository;
    private final VerificadorDeCompra verificadorDeCompra;

    @Transactional
    public void ejecutar(String compradorId, String ofertaId, String texto) {
        if (!verificadorDeCompra.haCompradoOferta(compradorId, ofertaId)) {
            throw new ReglaDominioException("El usuario debe haber comprado la oferta para dejar un comentario.");
        }
        Comentario comentario = Comentario.crear(UUID.randomUUID().toString(), ofertaId, compradorId, texto);
        comentarioRepository.guardar(comentario);
    }
}