package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.CompraDetalleResponse;
import com.uniquindio.ecommerce.application.usecase.ConsultarCompra;
import com.uniquindio.ecommerce.application.usecase.ConsultarHistorialDeCompras;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.CompraMapper;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class ConsultaComprasController {

    private final ConsultarHistorialDeCompras consultarHistorialUseCase;
    private final ConsultarCompra consultarCompraUseCase;
    private final CompraMapper mapper;

    @GetMapping
    public List<CompraDetalleResponse> listarMisCompras(
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(name = "estado", required = false) EstadoCompra estado,
            @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return consultarHistorialUseCase.ejecutar(usuario.getId(), desde, hasta, estado).stream()
                .map(mapper::toDetalleResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public CompraDetalleResponse obtener(@PathVariable String id,
                                         @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return mapper.toDetalleResponse(consultarCompraUseCase.ejecutar(id, usuario.getId()));
    }
}