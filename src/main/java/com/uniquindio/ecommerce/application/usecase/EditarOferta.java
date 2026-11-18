package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EditarOferta {

    private final OfertaRepository ofertaRepository;

    // imagenes null = no tocar la galería; una lista (aunque esté vacía) la reemplaza
    @Transactional
    public Oferta ejecutar(String ofertaId, String vendedorId, String titulo, Precio precio, int stock,
                           Plataforma plataforma, List<String> imagenes) {
        // Una oferta ajena se trata como inexistente
        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId)
                .filter(o -> o.perteneceA(vendedorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La oferta no existe"));

        oferta.editar(titulo, precio, stock, plataforma);
        if (imagenes != null) {
            oferta.definirImagenes(imagenes);
        }
        ofertaRepository.guardar(oferta);
        return oferta;
    }
}