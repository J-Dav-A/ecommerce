package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PerfilExtendidoTest {

    private Usuario usuario(String rol) {
        return Usuario.crear("u-1", "Ana", "ana@mail.com", "hash", null, rol, null);
    }

    @Test
    void elVendedorPuedeTenerFotoDescripcionYDatosDePago() {
        // Arrange
        Usuario vendedor = usuario("VENDEDOR");

        // Act
        vendedor.definirPerfilExtendido("https://img.test/yo.png", "Vendo claves originales", "Nequi 3001234567");

        // Assert
        assertEquals("https://img.test/yo.png", vendedor.getFotoPerfil());
        assertEquals("Vendo claves originales", vendedor.getDescripcion());
        assertEquals("Nequi 3001234567", vendedor.getDatosPago());
    }

    @Test
    void elCompradorSoloPuedeTenerFoto() {
        // Arrange
        Usuario comprador = usuario("COMPRADOR");

        // Act & Assert
        comprador.definirPerfilExtendido("https://img.test/yo.png", null, null);
        assertEquals("https://img.test/yo.png", comprador.getFotoPerfil());
        assertThrows(ReglaDominioException.class,
                () -> comprador.definirPerfilExtendido(null, "Una descripción", null));
        assertThrows(ReglaDominioException.class,
                () -> comprador.definirPerfilExtendido(null, null, "Mis datos de pago"));
    }

    @Test
    void rechazaFotosQueNoSeanUrlYTextosMuyLargos() {
        // Arrange
        Usuario vendedor = usuario("VENDEDOR");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> vendedor.definirPerfilExtendido("javascript:alert(1)", null, null));
        assertThrows(ReglaDominioException.class, () -> vendedor.definirPerfilExtendido(null, "a".repeat(1001), null));
        assertThrows(ReglaDominioException.class, () -> vendedor.definirPerfilExtendido(null, null, "a".repeat(501)));
    }

    @Test
    void losValoresVaciosBorranLosCampos() {
        // Arrange
        Usuario vendedor = usuario("VENDEDOR");
        vendedor.definirPerfilExtendido("https://img.test/yo.png", "Algo", "Algo");

        // Act
        vendedor.definirPerfilExtendido("  ", "", null);

        // Assert
        assertNull(vendedor.getFotoPerfil());
        assertNull(vendedor.getDescripcion());
        assertNull(vendedor.getDatosPago());
    }
}