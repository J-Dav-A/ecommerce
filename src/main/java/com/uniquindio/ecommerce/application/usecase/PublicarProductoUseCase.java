package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;

import java.util.UUID;

public class PublicarProductoUseCase {

    private final OfertaRepository repository;

    public PublicarProductoUseCase(OfertaRepository repository) {
        this.repository = repository;
    }

    public Oferta ejecutar(
            UUID id,
            UUID vendedorId,
            String titulo,
            Precio precio,
            int stock,
            Plataforma plataforma
    ) {

        Oferta oferta = new Oferta(
                id,
                vendedorId,
                titulo,
                precio,
                stock
        );

        oferta.definirPlataforma(plataforma);
        oferta.publicar();

        repository.guardar(oferta);

        return oferta;
    }
}