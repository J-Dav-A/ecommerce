package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.OfertaResponse;
import com.uniquindio.ecommerce.application.usecase.AgregarFavorito;
import com.uniquindio.ecommerce.application.usecase.ListarFavoritos;
import com.uniquindio.ecommerce.application.usecase.QuitarFavorito;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Siempre trabaja sobre los favoritos del comprador del token
@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final AgregarFavorito agregarFavorito;
    private final QuitarFavorito quitarFavorito;
    private final ListarFavoritos listarFavoritos;

    @GetMapping
    public List<OfertaResponse> misFavoritos(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return listarFavoritos.ejecutar(usuario.getId()).stream()
                .map(OfertaResponse::desde)
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