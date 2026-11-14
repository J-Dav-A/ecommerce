package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.FiltroBusqueda;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaVentas;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OfertaRepositoryEnMemoria implements OfertaRepository {

    private final Map<String, Oferta> ofertas = new ConcurrentHashMap<>();

    @Override
    public Optional<Oferta> obtenerPorId(String id) {
        return Optional.ofNullable(ofertas.get(id));
    }

    @Override
    public List<Oferta> obtenerPublicadas() {
        return ofertas.values().stream()
                .filter(o -> o.getEstado() == EstadoOferta.PUBLICADA)
                .toList();
    }

    @Override
    public List<Oferta> obtenerPorVendedorId(String vendedorId) {
        return ofertas.values().stream()
                .filter(o -> o.getVendedorId().equals(vendedorId) && o.getEstado() != EstadoOferta.ELIMINADA)
                .toList();
    }

    @Override
    public List<Oferta> obtenerTodasPorVendedorId(String vendedorId) {
        return ofertas.values().stream()
                .filter(o -> o.getVendedorId().equals(vendedorId))
                .toList();
    }

    @Override
    public Pagina<Oferta> buscar(FiltroBusqueda filtro, int pagina, int tamano) {
        String texto = filtro.texto();
        // La calificación mínima necesita los comentarios, que solo conoce la implementación JPA
        List<Oferta> filtradas = ofertas.values().stream()
                .filter(o -> o.getEstado() == EstadoOferta.PUBLICADA)
                .filter(o -> texto == null || texto.isBlank() || o.getTitulo().toLowerCase().contains(texto.toLowerCase()))
                .filter(o -> filtro.plataforma() == null || o.getPlataforma() == filtro.plataforma())
                .filter(o -> filtro.precioMinimo() == null || o.getPrecio().valor().compareTo(filtro.precioMinimo()) >= 0)
                .filter(o -> filtro.precioMaximo() == null || o.getPrecio().valor().compareTo(filtro.precioMaximo()) <= 0)
                .toList();

        int desde = Math.min(pagina * tamano, filtradas.size());
        int hasta = Math.min(desde + tamano, filtradas.size());
        List<Oferta> contenido = filtradas.subList(desde, hasta);
        int totalPaginas = (int) Math.ceil((double) filtradas.size() / tamano);

        return new Pagina<>(contenido, filtradas.size(), totalPaginas, pagina, tamano);
    }

    @Override
    public List<OfertaVentas> masVendidas(int limite) {
        return List.of(); // Solo se prueba con la implementación JPA real
    }

    @Override
    public void guardar(Oferta oferta) {
        ofertas.put(oferta.getId(), oferta);
    }
}