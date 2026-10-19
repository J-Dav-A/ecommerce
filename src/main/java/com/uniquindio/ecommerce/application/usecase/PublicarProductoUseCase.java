package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublicarProductoUseCase {

    private final OfertaRepository repository;

    @Transactional
    public Oferta ejecutar(String id, String vendedorId, String titulo, Precio precio, int stock, Plataforma plataforma) {
        Oferta oferta = Oferta.crear(id, vendedorId, titulo, precio, stock);
        oferta.definirPlataforma(plataforma);
        oferta.publicar();
        repository.guardar(oferta);
        return oferta;
    }
}