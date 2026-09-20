package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

public record LicenciaDigital(int maxActivaciones, boolean transferible) {

    public LicenciaDigital {
        if (maxActivaciones < 1) {
            throw new ReglaDominioException("La licencia debe permitir al menos una activación");
        }
    }
}
