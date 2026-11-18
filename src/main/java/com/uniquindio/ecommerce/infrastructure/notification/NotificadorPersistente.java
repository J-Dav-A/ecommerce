package com.uniquindio.ecommerce.infrastructure.notification;

import com.uniquindio.ecommerce.domain.entity.Comentario;
import com.uniquindio.ecommerce.domain.entity.Compra;
import com.uniquindio.ecommerce.domain.entity.Notificacion;
import com.uniquindio.ecommerce.domain.entity.Oferta;
import com.uniquindio.ecommerce.domain.repository.NotificacionRepository;
import com.uniquindio.ecommerce.domain.repository.OfertaRepository;
import com.uniquindio.ecommerce.domain.repository.UsuarioRepository;
import com.uniquindio.ecommerce.domain.service.EnviadorCorreo;
import com.uniquindio.ecommerce.domain.service.Notificador;
import com.uniquindio.ecommerce.domain.valueobject.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

// Guarda la notificación dentro de la aplicación y, cuando corresponde, también envía un correo.
// Un fallo al enviar el correo nunca debe deshacer la compra, el reembolso o el comentario.
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificadorPersistente implements Notificador {

    private final NotificacionRepository notificacionRepository;
    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnviadorCorreo enviadorCorreo;

    @Override
    public void ventaRealizada(Compra compra) {
        ofertaRepository.obtenerPorId(compra.getOfertaId()).ifPresent(oferta ->
                avisarEnLaApp(oferta.getVendedorId(), TipoNotificacion.VENTA_REALIZADA,
                        "Nueva compra de «" + oferta.getTitulo() + "»", compra.getId()));
    }

    @Override
    public void reembolsoSolicitado(Compra compra, String motivo) {
        ofertaRepository.obtenerPorId(compra.getOfertaId()).ifPresent(oferta -> {
            String mensaje = "Solicitaron reembolso de «" + oferta.getTitulo() + "»: " + motivo;
            avisarEnLaApp(oferta.getVendedorId(), TipoNotificacion.REEMBOLSO_SOLICITADO, mensaje, compra.getId());
            avisarPorCorreo(oferta.getVendedorId(), "Solicitud de reembolso", mensaje);
        });
    }

    @Override
    public void comentarioRecibido(Comentario comentario) {
        ofertaRepository.obtenerPorId(comentario.getOfertaId()).ifPresent((Oferta oferta) -> {
            String mensaje = "Nuevo comentario (" + comentario.getCalificacion() + " estrellas) en «"
                    + oferta.getTitulo() + "»";
            avisarEnLaApp(oferta.getVendedorId(), TipoNotificacion.COMENTARIO_NUEVO, mensaje, comentario.getId());
            avisarPorCorreo(oferta.getVendedorId(), "Nuevo comentario en tu oferta", mensaje);
        });
    }

    @Override
    public void compraConfirmada(Compra compra) {
        ofertaRepository.obtenerPorId(compra.getOfertaId()).ifPresent(oferta -> {
            String mensaje = "Tu compra de «" + oferta.getTitulo()
                    + "» fue confirmada. Ya puedes recibir tu clave digital desde la aplicación.";
            avisarEnLaApp(compra.getCompradorId(), TipoNotificacion.COMPRA_CONFIRMADA, mensaje, compra.getId());
            avisarPorCorreo(compra.getCompradorId(), "Compra confirmada", mensaje);
        });
    }

    private void avisarEnLaApp(String usuarioId, TipoNotificacion tipo, String mensaje, String referenciaId) {
        notificacionRepository.guardar(Notificacion.crear(
                UUID.randomUUID().toString(), usuarioId, tipo, mensaje, referenciaId, LocalDateTime.now()));
    }

    private void avisarPorCorreo(String usuarioId, String asunto, String mensaje) {
        try {
            usuarioRepository.obtenerPorId(usuarioId)
                    .ifPresent(u -> enviadorCorreo.enviar(u.getEmail(), asunto, mensaje));
        } catch (RuntimeException e) {
            log.warn("No se pudo enviar el correo '{}': {}", asunto, e.getMessage());
        }
    }
}