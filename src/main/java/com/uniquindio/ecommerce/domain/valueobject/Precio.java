package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

import java.math.BigDecimal;

public record Precio(BigDecimal valor) {

    public Precio {
        if (valor == null) {
            throw new ReglaDominioException("El valor del precio no puede ser nulo");
        }
        if (valor.signum() <= 0) {
            throw new ReglaDominioException("El valor del precio debe ser mayor que cero");
        }
    }
}
