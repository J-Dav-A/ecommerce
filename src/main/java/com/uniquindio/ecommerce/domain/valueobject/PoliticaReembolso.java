package com.uniquindio.ecommerce.domain.valueobject;

import java.time.Duration;

// Política de reembolso del marketplace: se puede pedir dentro de este plazo y solo si la clave no fue canjeada.
public final class PoliticaReembolso {

    public static final Duration PLAZO = Duration.ofHours(48);

    private PoliticaReembolso() {
    }
}