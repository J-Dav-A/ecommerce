package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoliticaContrasenaTest {

    private String mensajeDe(String contrasena) {
        return assertThrows(ReglaDominioException.class, () -> PoliticaContrasena.validar(contrasena)).getMessage();
    }

    @Test
    void aceptaUnaContrasenaDeOchoCaracteresConMayusculaYNumero() {
        // Act & Assert
        assertDoesNotThrow(() -> PoliticaContrasena.validar("Clave123"));
        assertDoesNotThrow(() -> PoliticaContrasena.validar("unaClaveMuyLarga9"));
    }

    @Test
    void rechazaLasContrasenasQueNoCumplenCadaRegla() {
        // Act & Assert
        assertEquals("La contraseña es obligatoria", mensajeDe(null));
        assertEquals("La contraseña es obligatoria", mensajeDe("   "));
        assertEquals("La contraseña debe tener al menos 8 caracteres", mensajeDe("Cl1"));
        assertEquals("La contraseña debe incluir al menos una letra mayúscula", mensajeDe("clave1234"));
        assertEquals("La contraseña debe incluir al menos un número", mensajeDe("ClaveSegura"));
    }
}