package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.RegistrarCompraRequest;
import com.uniquindio.ecommerce.application.dto.request.SolicitarReembolsoRequest;
import com.uniquindio.ecommerce.application.dto.response.CompraDetalleResponse;
import com.uniquindio.ecommerce.application.usecase.ConfirmarCompraUseCase;
import com.uniquindio.ecommerce.application.usecase.RegistrarCompra;
import com.uniquindio.ecommerce.application.usecase.SolicitarReembolso;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.CompraMapper;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final RegistrarCompra registrarCompraUseCase;
    private final ConfirmarCompraUseCase confirmarCompraUseCase;
    private final SolicitarReembolso solicitarReembolsoUseCase;
    private final CompraMapper mapper;

    @PostMapping
    public ResponseEntity<CompraDetalleResponse> crear(@Valid @RequestBody RegistrarCompraRequest request,
                                                       @AuthenticationPrincipal UsuarioPrincipal usuario) {
        Compra compra = registrarCompraUseCase.ejecutar(
                request.ofertaId(),
                usuario.getId()
        );

        CompraDetalleResponse response = mapper.toDetalleResponse(compra);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(compra.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmar(@PathVariable String id,
                                          @AuthenticationPrincipal UsuarioPrincipal usuario) {
        confirmarCompraUseCase.ejecutar(id, usuario.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reembolso")
    public ResponseEntity<Void> solicitarReembolso(@PathVariable String id,
                                                   @Valid @RequestBody SolicitarReembolsoRequest request,
                                                   @AuthenticationPrincipal UsuarioPrincipal usuario) {
        solicitarReembolsoUseCase.ejecutar(id, usuario.getId(), request.motivo());
        return ResponseEntity.ok().build();
    }
}