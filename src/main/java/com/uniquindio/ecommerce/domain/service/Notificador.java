package com.uniquindio.ecommerce.domain.service;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Compra;

// Puerto de salida: avisa a vendedores y compradores de lo que pasa con sus compras y ofertas.
// Los casos de uso solo dicen "qué pasó"; el adaptador decide cómo avisar (en la app, por correo...).
public interface Notificador {

    void ventaRealizada(Compra compra);

    void reembolsoSolicitado(Compra compra, String motivo);

    void comentarioRecibido(Comentario comentario);

    void compraConfirmada(Compra compra);

    // Valor por defecto de los casos de uso cuando no se inyecta uno (por ejemplo, en pruebas unitarias)
    Notificador NINGUNO = new Notificador() {
        @Override public void ventaRealizada(Compra compra) {}
        @Override public void reembolsoSolicitado(Compra compra, String motivo) {}
        @Override public void comentarioRecibido(Comentario comentario) {}
        @Override public void compraConfirmada(Compra compra) {}
    };
}