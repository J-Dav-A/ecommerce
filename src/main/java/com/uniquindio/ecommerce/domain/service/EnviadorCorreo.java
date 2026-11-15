package com.uniquindio.ecommerce.domain.service;

// Puerto de salida para enviar correos electrónicos
public interface EnviadorCorreo {
    void enviar(String destinatario, String asunto, String cuerpo);
}