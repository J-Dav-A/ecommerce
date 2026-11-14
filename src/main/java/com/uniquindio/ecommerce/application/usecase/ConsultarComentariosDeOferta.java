package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarComentariosDeOferta {

    public record ResumenComentarios(double promedioCalificacion, List<Comentario> comentarios) {}

    private final ComentarioRepository comentarioRepository;

    @Transactional(readOnly = true)
    public ResumenComentarios ejecutar(String ofertaId) {
        List<Comentario> comentarios = comentarioRepository.buscarPorOfertaId(ofertaId);
        double promedio = comentarios.stream().mapToInt(Comentario::getCalificacion).average().orElse(0);
        return new ResumenComentarios(promedio, comentarios);
    }
}