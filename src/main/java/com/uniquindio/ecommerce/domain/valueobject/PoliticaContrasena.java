package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

// Regla de contraseñas: mínimo 8 caracteres, con al menos una mayúscula y un número.
public final class PoliticaContrasena {

    public static final int LONGITUD_MINIMA = 8;

    private PoliticaContrasena() {}

    public static void validar(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new ReglaDominioException("La contraseña es obligatoria");
        }
        if (contrasena.length() < LONGITUD_MINIMA) {
            throw new ReglaDominioException("La contraseña debe tener al menos " + LONGITUD_MINIMA + " caracteres");
        }
        if (contrasena.chars().noneMatch(Character::isUpperCase)) {
            throw new ReglaDominioException("La contraseña debe incluir al menos una letra mayúscula");
        }
        if (contrasena.chars().noneMatch(Character::isDigit)) {
            throw new ReglaDominioException("La contraseña debe incluir al menos un número");
        }
    }
}