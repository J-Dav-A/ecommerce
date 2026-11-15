package com.uniquindio.ecommerce.infrastructure.mail;

import com.uniquindio.ecommerce.domain.service.EnviadorCorreo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

// Por defecto no envía correos reales: los escribe en el log (útil en desarrollo y en las pruebas).
// Para enviarlos de verdad se activa app.correo.habilitado=true (ver EnviadorCorreoSmtp).
@Slf4j
@Component
@ConditionalOnProperty(name = "app.correo.habilitado", havingValue = "false", matchIfMissing = true)
public class EnviadorCorreoRegistro implements EnviadorCorreo {

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        log.info("[CORREO SIMULADO] Para: {} | Asunto: {} | Mensaje: {}", destinatario, asunto, cuerpo);
    }
}