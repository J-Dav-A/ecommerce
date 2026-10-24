package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.DejarComentarioRequest;
import com.uniquindio.ecommerce.application.dto.response.ComentarioResponse;
import com.uniquindio.ecommerce.application.usecase.DejarComentario;
import com.uniquindio.ecommerce.domain.entity.Comentario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final DejarComentario dejarComentario;

    @PostMapping
    public ResponseEntity<ComentarioResponse> crear(@Valid @RequestBody DejarComentarioRequest request) {
        Comentario comentario = dejarComentario.ejecutar(request.compradorId(), request.ofertaId(), request.texto());

        ComentarioResponse response = ComentarioResponse.desde(comentario);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(comentario.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<ComentarioResponse> listarPorOferta(@RequestParam("ofertaId") String ofertaId) {
        return dejarComentario.listarPorOferta(ofertaId).stream()
                .map(ComentarioResponse::desde)
                .toList();
    }
}