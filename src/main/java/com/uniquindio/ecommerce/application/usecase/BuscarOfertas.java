package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaVentas;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarOfertas {

    private static final int TAMANO_MAXIMO = 50;
    private static final int RANKING_MAXIMO = 20;

    private final OfertaRepository ofertaRepository;

    @Transactional(readOnly = true)
    public Pagina<Oferta> buscar(String texto, Plataforma plataforma, int pagina, int tamano) {
        if (pagina < 0) {
            throw new ReglaDominioException("La página no puede ser negativa");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new ReglaDominioException("El tamaño de página debe estar entre 1 y " + TAMANO_MAXIMO);
        }
        return ofertaRepository.buscar(texto, plataforma, pagina, tamano);
    }

    @Transactional(readOnly = true)
    public List<OfertaVentas> masVendidas(int limite) {
        if (limite < 1 || limite > RANKING_MAXIMO) {
            throw new ReglaDominioException("El límite debe estar entre 1 y " + RANKING_MAXIMO);
        }
        return ofertaRepository.masVendidas(limite);
    }
}