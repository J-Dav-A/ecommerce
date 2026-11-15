package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.service.Notificador;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmarCompraUseCase {

    private final CompraRepository repository;

    // Se inyecta en la aplicación; en las pruebas unitarias queda en "no avisar a nadie"
    private Notificador notificador = Notificador.NINGUNO;

    @Autowired(required = false)
    public void setNotificador(Notificador notificador) {
        this.notificador = notificador;
    }

    @Transactional
    public void ejecutar(String compraId, String compradorId) {
        // Una compra ajena se trata como inexistente
        Compra compra = repository.obtenerPorId(compraId)
                .filter(c -> c.perteneceA(compradorId))
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no existe"));
        compra.confirmar();
        repository.guardar(compra);
        notificador.compraConfirmada(compra);
    }
}