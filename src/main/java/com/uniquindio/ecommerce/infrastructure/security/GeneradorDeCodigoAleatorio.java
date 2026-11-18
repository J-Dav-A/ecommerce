package com.uniquindio.ecommerce.infrastructure.security;

import com.uniquindio.ecommerce.domain.service.GeneradorDeCodigo;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GeneradorDeCodigoAleatorio implements GeneradorDeCodigo {

    private final SecureRandom random = new SecureRandom();

    // Seis dígitos, con ceros a la izquierda si hace falta
    @Override
    public String nuevo() {
        return String.format("%06d", random.nextInt(1_000_000));
    }
}