package com.uniquindio.ecommerce.domain.service;

// Genera el código numérico de recuperación de contraseña (separado para poder fijarlo en las pruebas)
public interface GeneradorDeCodigo {
    String nuevo();
}