package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarOfertasDelVendedor {

    private final OfertaRepository ofertaRepository;

    public List<Oferta> ejecutar(String vendedorId) {
        return ofertaRepository.obtenerPorVendedorId(vendedorId);
    }
}