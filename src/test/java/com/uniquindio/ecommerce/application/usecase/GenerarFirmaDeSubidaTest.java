package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.dto.response.FirmaSubidaResponse;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.exception.ServicioNoDisponibleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GenerarFirmaDeSubidaTest {

    private final GenerarFirmaDeSubida configurado = new GenerarFirmaDeSubida("mi-nube", "12345", "abc123secreto");

    @Test
    void firmaLosParametrosConSha1DeFormaQueCloudinaryLaAcepta() {
        // Act
        FirmaSubidaResponse firma = configurado.ejecutar("ofertas", 1700000000L);

        // Assert: SHA-1 de "folder=ecommerce/ofertas&timestamp=1700000000" + secreto
        assertEquals("375ccb659294306991ec405803f4c164ffd7380a", firma.firma());
        assertEquals("ecommerce/ofertas", firma.carpeta());
        assertEquals("mi-nube", firma.cloudName());
        assertEquals("12345", firma.apiKey());
        assertEquals("https://api.cloudinary.com/v1_1/mi-nube/image/upload", firma.urlDeSubida());
    }

    @Test
    void laRespuestaNuncaIncluyeElSecretoDeLaApi() {
        // Act
        FirmaSubidaResponse firma = configurado.ejecutar("perfiles", 1700000000L);

        // Assert
        assertFalse(firma.toString().contains("abc123secreto"));
    }

    @Test
    void soloAceptaLasCarpetasPermitidas() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> configurado.ejecutar("../etc", 1L));
        assertThrows(ReglaDominioException.class, () -> configurado.ejecutar(null, 1L));
    }

    @Test
    void sinConfiguracionResponde503EnLugarDeFirmarConUnSecretoVacio() {
        // Arrange
        GenerarFirmaDeSubida sinConfigurar = new GenerarFirmaDeSubida("", "", "");

        // Act & Assert
        assertThrows(ServicioNoDisponibleException.class, () -> sinConfigurar.ejecutar("ofertas", 1L));
    }
}