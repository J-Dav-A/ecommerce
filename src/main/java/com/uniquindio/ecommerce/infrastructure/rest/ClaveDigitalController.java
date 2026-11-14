package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.CargarClaveRequest;
import com.uniquindio.ecommerce.application.dto.response.ClaveResponse;
import com.uniquindio.ecommerce.application.usecase.CargarClaveDigital;
import com.uniquindio.ecommerce.application.usecase.EntregarClaveDigital;
import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClaveDigitalController {

    private final CargarClaveDigital cargarClaveDigital;
    private final EntregarClaveDigital entregarClaveDigital;

    // El vendedor dueño de la oferta le carga claves para vender
    @PostMapping("/ofertas/{ofertaId}/claves")
    public ResponseEntity<ClaveResponse> cargar(@PathVariable String ofertaId,
                                                @Valid @RequestBody CargarClaveRequest request,
                                                @AuthenticationPrincipal UsuarioPrincipal usuario) {
        ClaveDigital clave = cargarClaveDigital.ejecutar(
                ofertaId, usuario.getId(), request.codigo(), request.maxActivaciones(), request.transferible());
        return ResponseEntity.status(HttpStatus.CREATED).body(ClaveResponse.desde(clave));
    }

    // El comprador recibe la clave de una compra completada (queda canjeada: ya no se puede reembolsar)
    @PostMapping("/compras/{compraId}/clave")
    public ClaveResponse recibirClave(@PathVariable String compraId,
                                      @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return ClaveResponse.desde(entregarClaveDigital.ejecutar(compraId, usuario.getId()));
    }
}