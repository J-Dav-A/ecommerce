package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.repository.ComentarioRepository;
import com.uniquindio.ecommerce.domain.repository.ResumenPorOferta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultarCalificaciones {

    private final ComentarioRepository comentarioRepository;

    // Una sola consulta para toda una página de ofertas; las que no tienen comentarios no aparecen en el mapa
    @Transactional(readOnly = true)
    public Map<String, ResumenPorOferta> ejecutar(Collection<String> ofertaIds) {
        return comentarioRepository.resumenPorOfertas(ofertaIds).stream()
                .collect(Collectors.toMap(ResumenPorOferta::ofertaId, Function.identity()));
    }
}