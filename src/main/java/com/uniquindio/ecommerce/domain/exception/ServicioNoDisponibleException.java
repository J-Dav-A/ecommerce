package com.uniquindio.ecommerce.domain.exception;

// Un servicio externo necesario no está configurado o no responde (503)
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}