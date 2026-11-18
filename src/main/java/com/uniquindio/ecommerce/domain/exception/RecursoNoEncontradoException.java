package com.uniquindio.ecommerce.domain.exception;

// Se lanza cuando un recurso pedido por id no existe (oferta, compra, clave...).
// Se mantiene separada de ReglaDominioException: una cosa es "no existe" (404)
// y otra es "existe pero la regla de negocio no lo permite" (422).
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}