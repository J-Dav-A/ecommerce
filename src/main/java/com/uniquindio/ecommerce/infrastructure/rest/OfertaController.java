package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.PublicarOfertaRequest;
import com.uniquindio.ecommerce.application.dto.response.OfertaResponse;
import com.uniquindio.ecommerce.application.dto.response.OfertaVentaResponse;
import com.uniquindio.ecommerce.application.dto.response.PageResponse;
import com.uniquindio.ecommerce.application.usecase.BuscarOfertas;
import com.uniquindio.ecommerce.application.usecase.EliminarOferta;
import com.uniquindio.ecommerce.application.usecase.PublicarProductoUseCase;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
public class OfertaController {

    private final BuscarOfertas buscarOfertas;
    private final PublicarProductoUseCase publicarProductoUseCase;
    private final EliminarOferta eliminarOferta;

    @GetMapping
    public PageResponse<OfertaResponse> buscar(
            @RequestParam(name = "busqueda", required = false) String busqueda,
            @RequestParam(name = "plataforma", required = false) Plataforma plataforma,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return PageResponse.desde(
                buscarOfertas.buscar(busqueda, plataforma, page, size),
                OfertaResponse::desde);
    }

    @GetMapping("/mas-vendidas")
    public List<OfertaVentaResponse> masVendidas(@RequestParam(name = "limite", defaultValue = "3") int limite) {
        return buscarOfertas.masVendidas(limite).stream()
                .map(OfertaVentaResponse::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public OfertaResponse obtener(@PathVariable String id) {
        return OfertaResponse.desde(buscarOfertas.obtener(id));
    }

    @PostMapping
    public ResponseEntity<OfertaResponse> publicar(@Valid @RequestBody PublicarOfertaRequest request) {
        Oferta oferta = publicarProductoUseCase.ejecutar(
                UUID.randomUUID().toString(),
                request.vendedorId(),
                request.titulo(),
                new Precio(request.precioValor(), request.precioMoneda()),
                request.stock(),
                request.plataforma());

        OfertaResponse response = OfertaResponse.desde(oferta);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(oferta.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        eliminarOferta.ejecutar(id);
        return ResponseEntity.ok().build();
    }
}