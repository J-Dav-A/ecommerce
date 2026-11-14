package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.response.ComentarioResponse;
import com.uniquindio.ecommerce.application.dto.response.MetricaOfertaResponse;
import com.uniquindio.ecommerce.application.dto.response.OfertaResponse;
import com.uniquindio.ecommerce.application.usecase.ConsultarMetricasDelVendedor;
import com.uniquindio.ecommerce.application.usecase.ListarComentariosDelVendedor;
import com.uniquindio.ecommerce.application.usecase.ListarOfertasDelVendedor;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vendedor")
@RequiredArgsConstructor
public class VendedorController {

    private final ListarOfertasDelVendedor listarOfertasDelVendedor;
    private final ListarComentariosDelVendedor listarComentariosDelVendedor;
    private final ConsultarMetricasDelVendedor consultarMetricasDelVendedor;

    @GetMapping("/ofertas")
    public List<OfertaResponse> misOfertas(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return listarOfertasDelVendedor.ejecutar(usuario.getId()).stream()
                .map(OfertaResponse::desde)
                .toList();
    }

    // Comentarios recibidos en mis ofertas; con sinResponder=true solo los pendientes de respuesta
    @GetMapping("/comentarios")
    public List<ComentarioResponse> misComentarios(
            @RequestParam(name = "sinResponder", defaultValue = "false") boolean sinResponder,
            @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return listarComentariosDelVendedor.ejecutar(usuario.getId(), sinResponder).stream()
                .map(ComentarioResponse::desde)
                .toList();
    }

    // Ventas y calificación promedio por oferta, con rango de fechas opcional (formato AAAA-MM-DD)
    @GetMapping("/metricas")
    public List<MetricaOfertaResponse> metricas(
            @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return consultarMetricasDelVendedor.ejecutar(usuario.getId(), desde, hasta).stream()
                .map(MetricaOfertaResponse::desde)
                .toList();
    }
}