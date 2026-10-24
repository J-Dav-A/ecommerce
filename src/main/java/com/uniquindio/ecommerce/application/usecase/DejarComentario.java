package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.service.VerificadorDeCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DejarComentario {

    private final ComentarioRepository comentarioRepository;
    private final VerificadorDeCompra verificadorDeCompra;

    @Transactional
    public Comentario ejecutar(String compradorId, String ofertaId, String texto) {
        if (!verificadorDeCompra.haCompradoOferta(compradorId, ofertaId)) {
            throw new ReglaDominioException("El usuario debe haber comprado la oferta para dejar un comentario.");
        }
        Comentario comentario = Comentario.crear(UUID.randomUUID().toString(), compradorId, ofertaId, texto);
        comentarioRepository.guardar(comentario);
        return comentario;
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarPorOferta(String ofertaId) {
        return comentarioRepository.buscarPorOfertaId(ofertaId);
    }
}