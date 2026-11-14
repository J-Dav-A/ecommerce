package com.uniquindio.ecommerce.domain.entity;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OfertaEdicionTest {

    private final Precio precio = new Precio(new BigDecimal("30"), "USD");
    private Oferta oferta;

    @BeforeEach
    void preparar() {
        oferta = Oferta.crear("o-1", "v-1", "Hades", precio, 5);
        oferta.definirPlataforma(Plataforma.PC);
        oferta.publicar();
    }

    @Test
    void deberiaEditarLosDatosComercialesSinCambiarVendedorNiEstado() {
        // Act
        oferta.editar("Hades II", new Precio(new BigDecimal("45"), "USD"), 8, Plataforma.NINTENDO);

        // Assert
        assertEquals("Hades II", oferta.getTitulo());
        assertEquals(new BigDecimal("45"), oferta.getPrecio().valor());
        assertEquals(8, oferta.getStock());
        assertEquals(Plataforma.NINTENDO, oferta.getPlataforma());
        assertEquals("v-1", oferta.getVendedorId());
        assertEquals(EstadoOferta.PUBLICADA, oferta.getEstado());
    }

    @Test
    void noDebeAceptarDatosInvalidosAlEditar() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> oferta.editar(" ", precio, 5, Plataforma.PC));
        assertThrows(ReglaDominioException.class, () -> oferta.editar("X", null, 5, Plataforma.PC));
        assertThrows(ReglaDominioException.class, () -> oferta.editar("X", precio, -1, Plataforma.PC));
        assertThrows(ReglaDominioException.class, () -> oferta.editar("X", precio, 5, null));
        assertEquals("Hades", oferta.getTitulo());
    }

    @Test
    void unaOfertaPublicadaNoPuedeQuedarseSinStock() {
        // Act
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class,
                () -> oferta.editar("Hades", precio, 0, Plataforma.PC));

        // Assert
        assertEquals("Una oferta publicada necesita stock disponible", excepcion.getMessage());
        assertEquals(5, oferta.getStock());
    }

    @Test
    void noDebeEditarUnaOfertaEliminada() {
        // Arrange
        oferta.eliminar();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> oferta.editar("Otro", precio, 5, Plataforma.PC));
        assertThrows(ReglaDominioException.class, () -> oferta.definirImagenes(List.of("https://img.test/a.png")));
    }

    @Test
    void deberiaGuardarLasImagenesComoUrlsYReemplazarLaGaleria() {
        // Act
        oferta.definirImagenes(List.of("https://img.test/a.png", "http://img.test/b.jpg"));
        oferta.definirImagenes(List.of("https://img.test/c.png"));

        // Assert
        assertEquals(List.of("https://img.test/c.png"), oferta.getImagenes());
    }

    @Test
    void admiteMaximoDiezImagenes() {
        // Arrange
        List<String> diez = new ArrayList<>();
        for (int i = 0; i < 10; i++) diez.add("https://img.test/" + i + ".png");

        // Act & Assert
        oferta.definirImagenes(diez);
        assertEquals(10, oferta.getImagenes().size());

        diez.add("https://img.test/extra.png");
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class, () -> oferta.definirImagenes(diez));
        assertEquals("Una oferta admite máximo 10 imágenes", excepcion.getMessage());
        assertEquals(10, oferta.getImagenes().size());
    }

    @Test
    void rechazaUrlsQueNoSeanHttpONiVacias() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> oferta.definirImagenes(List.of("ftp://img.test/a.png")));
        assertThrows(ReglaDominioException.class, () -> oferta.definirImagenes(List.of("no es una url")));
        assertThrows(ReglaDominioException.class, () -> oferta.definirImagenes(List.of("")));
        assertThrows(ReglaDominioException.class, () -> oferta.definirImagenes(List.of("https://img.test/" + "a".repeat(500))));
        assertTrue(oferta.getImagenes().isEmpty());
    }
}