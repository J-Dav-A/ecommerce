package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.usecase.ConfirmarCompraUseCase;
import com.uniquindio.ecommerce.application.usecase.RegistrarCompra;
import com.uniquindio.ecommerce.application.usecase.SolicitarReembolso;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.Precio;
import com.uniquindio.ecommerce.infrastructure.rest.mapper.CompraMapper;
import com.uniquindio.ecommerce.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Aquí el caso de uso ConfirmarCompraUseCase es el REAL (se trae con @Import) y lo que se simula es el
// repositorio. Así se puede comprobar de verdad que guardar() nunca se llama cuando algo falla.
// El GlobalExceptionHandler real es el que convierte cada excepción en 404 o 400.
// addFilters = false quita la seguridad solo en este test: la seguridad se prueba aparte.
@WebMvcTest(
        controllers = CompraController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import({ConfirmarCompraUseCase.class, CompraMapper.class})
class CompraControllerErroresTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompraRepository compraRepository;

    // Otros colaboradores del controller que estas pruebas no usan
    @MockitoBean
    private RegistrarCompra registrarCompra;

    @MockitoBean
    private SolicitarReembolso solicitarReembolso;

    @Test
    void deberiaResponder404YNoGuardarCuandoLaCompraNoExiste() throws Exception {
        // Arrange
        when(compraRepository.obtenerPorId("c-404")).thenReturn(Optional.empty());

        // Act
        ResultActions respuesta = mockMvc.perform(put("/api/compras/{id}/confirmar", "c-404"));

        // Assert
        respuesta.andExpect(status().isNotFound());
        verify(compraRepository, never()).guardar(any());
    }

    @Test
    void deberiaResponder400YNoGuardarCuandoSeRompeUnaReglaDeNegocio() throws Exception {
        // Arrange: una compra ya REEMBOLSADA no puede confirmarse (regla de dominio)
        Compra reembolsada = Compra.reconstruir("c-1", "o-101", "u-2",
                new Precio(new BigDecimal("59.99"), "USD"), LocalDateTime.now(), EstadoCompra.REEMBOLSADA);
        when(compraRepository.obtenerPorId("c-1")).thenReturn(Optional.of(reembolsada));

        // Act
        ResultActions respuesta = mockMvc.perform(put("/api/compras/{id}/confirmar", "c-1"));

        // Assert
        respuesta.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Una compra reembolsada no puede modificarse"));
        verify(compraRepository, never()).guardar(any());
    }

    @Test
    void noDeberiaLlamarAlCasoDeUsoSiLosDatosDeLaCompraSonInvalidos() throws Exception {
        // Arrange: cuerpo vacío, faltan ofertaId y compradorId

        // Act
        ResultActions respuesta = mockMvc.perform(post("/api/compras")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        // Assert
        respuesta.andExpect(status().isBadRequest());
        verify(registrarCompra, never()).ejecutar(any(), any());
    }
}