package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.DejarComentarioRequest;
import com.uniquindio.ecommerce.application.dto.request.ResponderComentarioRequest;
import com.uniquindio.ecommerce.application.dto.response.ComentarioResponse;
import com.uniquindio.ecommerce.application.dto.response.ResumenComentariosResponse;
import com.uniquindio.ecommerce.application.usecase.ConsultarComentariosDeOferta;
import com.uniquindio.ecommerce.application.usecase.ConsultarComentariosDeOferta.ResumenComentarios;
import com.uniquindio.ecommerce.application.usecase.DejarComentario;
import com.uniquindio.ecommerce.application.usecase.ResponderComentario;
import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final DejarComentario dejarComentario;
    private final ConsultarComentariosDeOferta consultarComentariosDeOferta;
    private final ResponderComentario responderComentario;

    @PostMapping
    public ResponseEntity<ComentarioResponse> crear(@Valid @RequestBody DejarComentarioRequest request,
                                                    @AuthenticationPrincipal UsuarioPrincipal usuario) {
        Comentario comentario = dejarComentario.ejecutar(
                usuario.getId(), request.ofertaId(), request.texto(), request.calificacion());

        ComentarioResponse response = ComentarioResponse.desde(comentario);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(comentario.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // Público, como el detalle de la oferta: comentarios (el más reciente primero) y calificación promedio
    @GetMapping
    public ResumenComentariosResponse listarPorOferta(@RequestParam("ofertaId") String ofertaId) {
        ResumenComentarios resumen = consultarComentariosDeOferta.ejecutar(ofertaId);
        double promedio = Math.round(resumen.promedioCalificacion() * 100) / 100.0;
        return new ResumenComentariosResponse(
                promedio,
                resumen.comentarios().size(),
                resumen.comentarios().stream().map(ComentarioResponse::desde).toList());
    }

    // El vendedor dueño de la oferta responde un comentario (una sola vez)
    @PutMapping("/{id}/respuesta")
    public ComentarioResponse responder(@PathVariable String id,
                                        @Valid @RequestBody ResponderComentarioRequest request,
                                        @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return ComentarioResponse.desde(responderComentario.ejecutar(id, usuario.getId(), request.respuesta()));
    }
}