package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.EditarOfertaRequest;
import com.uniquindio.ecommerce.application.dto.request.PublicarOfertaRequest;
import com.uniquindio.ecommerce.application.dto.response.OfertaResponse;
import com.uniquindio.ecommerce.application.dto.response.OfertaVentaResponse;
import com.uniquindio.ecommerce.application.dto.response.PageResponse;
import com.uniquindio.ecommerce.application.usecase.BuscarOfertas;
import com.uniquindio.ecommerce.application.usecase.ConsultarCalificaciones;
import com.uniquindio.ecommerce.application.usecase.EditarOferta;
import com.uniquindio.ecommerce.application.usecase.EliminarOferta;
import com.uniquindio.ecommerce.application.usecase.PublicarProductoUseCase;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.FiltroBusqueda;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.repository.ResumenPorOferta;
import com.uniquindio.ecommerce.domain.valueobject.OrdenOfertas;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ofertas")
@RequiredArgsConstructor
public class OfertaController {

    private final BuscarOfertas buscarOfertas;
    private final PublicarProductoUseCase publicarProductoUseCase;
    private final EliminarOferta eliminarOferta;
    private final EditarOferta editarOferta;
    private final ConsultarCalificaciones consultarCalificaciones;

    @GetMapping
    public PageResponse<OfertaResponse> buscar(
            @RequestParam(name = "busqueda", required = false) String busqueda,
            @RequestParam(name = "plataforma", required = false) Plataforma plataforma,
            @RequestParam(name = "precioMinimo", required = false) BigDecimal precioMinimo,
            @RequestParam(name = "precioMaximo", required = false) BigDecimal precioMaximo,
            @RequestParam(name = "calificacionMinima", required = false) Double calificacionMinima,
            @RequestParam(name = "orden", required = false) OrdenOfertas orden,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Pagina<Oferta> pagina = buscarOfertas.buscar(
                new FiltroBusqueda(busqueda, plataforma, precioMinimo, precioMaximo, calificacionMinima, orden), page, size);
        Map<String, ResumenPorOferta> calificaciones = consultarCalificaciones.ejecutar(
                pagina.contenido().stream().map(Oferta::getId).toList());
        return PageResponse.desde(pagina, o -> OfertaResponse.desde(o, calificaciones.get(o.getId())));
    }

    @GetMapping("/mas-vendidas")
    public List<OfertaVentaResponse> masVendidas(@RequestParam(name = "limite", defaultValue = "3") int limite) {
        return buscarOfertas.masVendidas(limite).stream()
                .map(OfertaVentaResponse::desde)
                .toList();
    }

    @GetMapping("/{id}")
    public OfertaResponse obtener(@PathVariable String id) {
        Oferta oferta = buscarOfertas.obtener(id);
        return OfertaResponse.desde(oferta, consultarCalificaciones.ejecutar(List.of(id)).get(id));
    }

    @PostMapping
    public ResponseEntity<OfertaResponse> publicar(@Valid @RequestBody PublicarOfertaRequest request,
                                                   @AuthenticationPrincipal UsuarioPrincipal usuario) {
        Oferta oferta = publicarProductoUseCase.ejecutar(
                UUID.randomUUID().toString(),
                usuario.getId(),
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

    // Solo el vendedor dueño; el vendedor, el id y el estado no se pueden cambiar
    @PutMapping("/{id}")
    public OfertaResponse editar(@PathVariable String id,
                                 @Valid @RequestBody EditarOfertaRequest request,
                                 @AuthenticationPrincipal UsuarioPrincipal usuario) {
        Oferta oferta = editarOferta.ejecutar(
                id,
                usuario.getId(),
                request.titulo(),
                new Precio(request.precioValor(), request.precioMoneda()),
                request.stock(),
                request.plataforma(),
                request.imagenes());
        return OfertaResponse.desde(oferta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id,
                                         @AuthenticationPrincipal UsuarioPrincipal usuario) {
        eliminarOferta.ejecutar(id, usuario.getId());
        return ResponseEntity.ok().build();
    }
}