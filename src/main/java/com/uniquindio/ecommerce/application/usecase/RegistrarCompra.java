package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrarCompra {

    private final OfertaRepository ofertaRepository;
    private final CompraRepository compraRepository;
    private final ClaveDigitalRepository claveDigitalRepository;

    @Transactional
    public Compra ejecutar(String ofertaId, String compradorId) {
        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId).orElseThrow();
        ClaveDigital clave = claveDigitalRepository.obtenerDisponiblePorOferta(ofertaId).orElseThrow();

        Compra compra = Compra.realizar(UUID.randomUUID().toString(), ofertaId, compradorId, oferta.getPrecio());
        compra.confirmar();
        oferta.descontarStock(1);
        clave.asignar(compradorId, Instant.now());

        compraRepository.guardar(compra);
        ofertaRepository.guardar(oferta);
        claveDigitalRepository.guardar(clave);

        return compra;
    }
}