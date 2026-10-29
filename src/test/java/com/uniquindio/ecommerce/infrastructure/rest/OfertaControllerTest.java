package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.usecase.BuscarOfertas;
import com.uniquindio.ecommerce.application.usecase.EliminarOferta;
import com.uniquindio.ecommerce.application.usecase.PublicarProductoUseCase;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.valueobject.Plataforma;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
        controllers = OfertaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.uniquindio.ecommerce.infrastructure.security.JwtAuthenticationFilter.class)
)
class OfertaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuscarOfertas buscarOfertas;

    @MockitoBean
    private PublicarProductoUseCase publicarProductoUseCase;

    @MockitoBean
    private EliminarOferta eliminarOferta;

    @Test
    void alConsultarUnaOfertaExistenteDevuelve200ConSusDatos() throws Exception {
        Oferta oferta = Oferta.crear("o-1", "u-1", "Zelda", new Precio(new BigDecimal("59.99"), "USD"), 5);
        oferta.definirPlataforma(Plataforma.NINTENDO);
        oferta.publicar();

        when(buscarOfertas.obtener("o-1")).thenReturn(oferta);

        mockMvc.perform(get("/api/ofertas/{id}", "o-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("o-1"))
                .andExpect(jsonPath("$.titulo").value("Zelda"))
                .andExpect(jsonPath("$.estado").value("PUBLICADA"));
    }
}