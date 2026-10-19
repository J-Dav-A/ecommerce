package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import java.math.BigDecimal;

public record Precio(BigDecimal valor, String moneda) {
    public Precio {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaDominioException("El precio debe ser mayor a cero");
        }
        if (moneda == null || moneda.isBlank()) {
            throw new ReglaDominioException("La moneda es obligatoria");
        }
    }
}