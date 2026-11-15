package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.ClaveDigital;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.ClaveDigitalRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.service.Notificador;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Se inyecta en la aplicación; en las pruebas unitarias queda en "no avisar a nadie"
    private Notificador notificador = Notificador.NINGUNO;

    @Autowired(required = false)
    public void setNotificador(Notificador notificador) {
        this.notificador = notificador;
    }

    @Transactional
    public Compra ejecutar(String ofertaId, String compradorId) {
        Oferta oferta = ofertaRepository.obtenerPorId(ofertaId).orElseThrow(() -> new RecursoNoEncontradoException("La oferta no existe"));
        oferta.verificarDisponibleParaVenta();
        verificarQueNoLaTengaYaComprada(ofertaId, compradorId);
        ClaveDigital clave = claveDigitalRepository.obtenerDisponiblePorOferta(ofertaId).orElseThrow(() -> new ReglaDominioException("No hay claves digitales disponibles para esta oferta"));

        Compra compra = Compra.realizar(UUID.randomUUID().toString(), ofertaId, compradorId, oferta.getPrecio());
        oferta.descontarStock(1);
        clave.asignar(compradorId, Instant.now());

        compraRepository.guardar(compra);
        ofertaRepository.guardar(oferta);
        claveDigitalRepository.guardar(clave);
        notificador.ventaRealizada(compra);

        return compra;
    }

    private void verificarQueNoLaTengaYaComprada(String ofertaId, String compradorId) {
        boolean yaComprada = compraRepository.obtenerPorCompradorId(compradorId).stream()
                .anyMatch(c -> c.getOfertaId().equals(ofertaId) && c.estaActiva());
        if (yaComprada) {
            throw new ReglaDominioException("Ya compraste esta oferta");
        }
    }
}