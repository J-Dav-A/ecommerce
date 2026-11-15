package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.OfertaResponse;
import com.uniquindio.ecommerce.application.usecase.AgregarFavorito;
import com.uniquindio.ecommerce.application.usecase.ConsultarCalificaciones;
import com.uniquindio.ecommerce.application.usecase.ListarFavoritos;
import com.uniquindio.ecommerce.application.usecase.QuitarFavorito;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.ResumenPorOferta;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Siempre trabaja sobre los favoritos del comprador del token
@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final AgregarFavorito agregarFavorito;
    private final QuitarFavorito quitarFavorito;
    private final ListarFavoritos listarFavoritos;
    private final ConsultarCalificaciones consultarCalificaciones;

    @GetMapping
    public List<OfertaResponse> misFavoritos(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        List<Oferta> ofertas = listarFavoritos.ejecutar(usuario.getId());
        Map<String, ResumenPorOferta> calificaciones = consultarCalificaciones.ejecutar(
                ofertas.stream().map(Oferta::getId).toList());
        return ofertas.stream()
                .map(o -> OfertaResponse.desde(o, calificaciones.get(o.getId())))
                .toList();
    }

    @PostMapping("/{ofertaId}")
    public ResponseEntity<OfertaResponse> agregar(@PathVariable String ofertaId,
                                                  @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OfertaResponse.desde(agregarFavorito.ejecutar(usuario.getId(), ofertaId)));
    }

    @DeleteMapping("/{ofertaId}")
    public ResponseEntity<Void> quitar(@PathVariable String ofertaId,
                                       @AuthenticationPrincipal UsuarioPrincipal usuario) {
        quitarFavorito.ejecutar(usuario.getId(), ofertaId);
        return ResponseEntity.noContent().build();
    }
}