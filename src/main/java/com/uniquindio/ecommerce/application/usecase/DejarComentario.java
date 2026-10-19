package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.service.VerificadorDeCompra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DejarComentario {

    private final ComentarioRepository comentarioRepository;
    private final VerificadorDeCompra verificador;

    @Transactional
    public Comentario ejecutar(String compradorId, String ofertaId, String texto) {
        verificador.verificarQuePuedeComentar(compradorId, ofertaId);
        Comentario comentario = Comentario.crear(compradorId, ofertaId, texto);
        comentarioRepository.guardar(comentario);
        return comentario;
    }
}