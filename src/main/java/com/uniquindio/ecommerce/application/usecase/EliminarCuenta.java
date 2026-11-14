package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Usuario;
import com.uniquindio.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.repository.CompraRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.valueobject.EstadoCompra;
import com.uniquindio.ecommerce.domain.valueobject.EstadoOferta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarCuenta {

    private final UsuarioRepository usuarioRepository;
    private final OfertaRepository ofertaRepository;
    private final CompraRepository compraRepository;

    // Borrado lógico. No se puede eliminar la cuenta con ofertas publicadas ni con compras pendientes de confirmar.
    @Transactional
    public void ejecutar(String usuarioId) {
        Usuario usuario = usuarioRepository.obtenerPorId(usuarioId)
                .filter(Usuario::estaActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no existe"));

        boolean tieneOfertasPublicadas = ofertaRepository.obtenerPorVendedorId(usuarioId).stream()
                .anyMatch(o -> o.getEstado() == EstadoOferta.PUBLICADA);
        if (tieneOfertasPublicadas) {
            throw new ReglaDominioException("No puedes eliminar la cuenta con ofertas publicadas: elimínalas primero");
        }
        boolean tieneComprasPendientes = compraRepository.obtenerPorCompradorId(usuarioId).stream()
                .anyMatch(c -> c.getEstado() == EstadoCompra.PENDIENTE);
        if (tieneComprasPendientes) {
            throw new ReglaDominioException("No puedes eliminar la cuenta con compras pendientes de confirmar");
        }

        usuario.desactivar();
        usuarioRepository.guardar(usuario);
    }
}