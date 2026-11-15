package com.uniquindio.ecommerce.domain.valueobject;

public enum TipoNotificacion {
    VENTA_REALIZADA,        // al vendedor: alguien compró una de sus ofertas
    REEMBOLSO_SOLICITADO,   // al vendedor: un comprador pidió reembolso
    COMENTARIO_NUEVO,       // al vendedor: dejaron un comentario en una de sus ofertas
    COMPRA_CONFIRMADA       // al comprador: su compra quedó confirmada
}