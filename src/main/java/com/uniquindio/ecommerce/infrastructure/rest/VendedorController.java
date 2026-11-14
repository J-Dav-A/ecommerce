package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.OfertaResponse;
import com.uniquindio.ecommerce.application.usecase.ListarOfertasDelVendedor;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendedor")
@RequiredArgsConstructor
public class VendedorController {

    private final ListarOfertasDelVendedor listarOfertasDelVendedor;

    @GetMapping("/ofertas")
    public List<OfertaResponse> misOfertas(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return listarOfertasDelVendedor.ejecutar(usuario.getId()).stream()
                .map(OfertaResponse::desde)
                .toList();
    }
}