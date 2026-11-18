package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.NotificacionResponse;
import com.uniquindio.ecommerce.application.usecase.ConsultarNotificaciones;
import com.uniquindio.ecommerce.application.usecase.MarcarNotificaciones;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Las notificaciones son del usuario del token, sea comprador o vendedor
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final ConsultarNotificaciones consultarNotificaciones;
    private final MarcarNotificaciones marcarNotificaciones;

    @GetMapping
    public List<NotificacionResponse> listar(
            @RequestParam(name = "soloNoLeidas", defaultValue = "false") boolean soloNoLeidas,
            @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return consultarNotificaciones.listar(usuario.getId(), soloNoLeidas).stream()
                .map(NotificacionResponse::desde)
                .toList();
    }

    @GetMapping("/no-leidas/cantidad")
    public Map<String, Long> cantidadNoLeidas(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return Map.of("cantidad", consultarNotificaciones.contarNoLeidas(usuario.getId()));
    }

    @PutMapping("/{id}/leida")
    public ResponseEntity<Void> marcarLeida(@PathVariable String id,
                                            @AuthenticationPrincipal UsuarioPrincipal usuario) {
        marcarNotificaciones.marcarLeida(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/leidas")
    public Map<String, Integer> marcarTodasLeidas(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return Map.of("marcadas", marcarNotificaciones.marcarTodasLeidas(usuario.getId()));
    }
}