package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.CompraDetalleResponse;
import com.uniquindio.ecommerce.application.usecase.ConsultarHistorialDeCompras;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.CompraMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class ConsultaComprasController {

    private final ConsultarHistorialDeCompras consultarHistorialUseCase;
    private final CompraMapper mapper;

    @GetMapping
    public List<CompraDetalleResponse> listarPorComprador(@RequestParam("compradorId") String compradorId) {
        return consultarHistorialUseCase.ejecutar(compradorId).stream()
                .map(mapper::toDetalleResponse)
                .toList();
    }
}