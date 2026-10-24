package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Oferta;
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
    public Pagina<Oferta> buscar(String texto, Plataforma plataforma, int pagina, int tamano) {
        List<Oferta> filtradas = ofertas.values().stream()
                .filter(o -> o.getEstado() == EstadoOferta.PUBLICADA)
                .filter(o -> texto == null || texto.isBlank() || o.getTitulo().toLowerCase().contains(texto.toLowerCase()))
                .filter(o -> plataforma == null || o.getPlataforma() == plataforma)
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