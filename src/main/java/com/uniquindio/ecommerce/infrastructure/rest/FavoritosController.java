
package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.OfertaResponse;
import com.uniquindio.ecommerce.application.usecase.AgregarFavorito;
import com.uniquindio.ecommerce.application.usecase.ConsultarFavoritos;
import com.uniquindio.ecommerce.application.usecase.EliminarFavorito;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritosController {

    private final AgregarFavorito agregarFavorito;
    private final ConsultarFavoritos consultarFavoritos;
    private final EliminarFavorito eliminarFavorito;
    private final UsuarioJpaDataRepository usuarioRepository;

    @GetMapping
    public List<OfertaResponse> listar(Authentication authentication) {

        String compradorId = obtenerCompradorId(authentication);

        return consultarFavoritos.ejecutar(compradorId)
                .stream()
                .map(OfertaResponse::desde)
                .toList();
    }

    @PostMapping("/{ofertaId}")
    public ResponseEntity<OfertaResponse> agregar(
            @PathVariable String ofertaId,
            Authentication authentication
    ) {

        String compradorId = obtenerCompradorId(authentication);

        Oferta oferta = agregarFavorito.ejecutar(compradorId, ofertaId);

        return ResponseEntity.ok(OfertaResponse.desde(oferta));
    }

    @DeleteMapping("/{ofertaId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable String ofertaId,
            Authentication authentication
    ) {

        String compradorId = obtenerCompradorId(authentication);

        eliminarFavorito.ejecutar(compradorId, ofertaId);

        return ResponseEntity.noContent().build();
    }

    private String obtenerCompradorId(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró el usuario autenticado"
                ))
                .getId();
    }
}
