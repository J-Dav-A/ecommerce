package com.uniquindio.ecommerce.domain.valueobject;

public enum EstadoCompra {

    PENDIENTE,
    COMPLETADA,
    REEMBOLSADA;

    public boolean esFinal() {
        return this == REEMBOLSADA;
    }

    public boolean puedeTransicionarA(EstadoCompra siguiente) {

        return switch (this) {
            case PENDIENTE -> siguiente == COMPLETADA;
            case COMPLETADA -> siguiente == REEMBOLSADA;
            case REEMBOLSADA -> false;
        };
    }
}