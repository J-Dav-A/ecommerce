package com.uniquindio.ecommerce.infrastructure.mail;

import com.uniquindio.ecommerce.domain.service.EnviadorCorreo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// Envío real por SMTP; se activa con app.correo.habilitado=true y los datos spring.mail.* (variables SMTP_*)
@Component
@ConditionalOnProperty(name = "app.correo.habilitado", havingValue = "true")
public class EnviadorCorreoSmtp implements EnviadorCorreo {

    private final JavaMailSender mailSender;
    private final String remitente;

    public EnviadorCorreoSmtp(JavaMailSender mailSender, @Value("${app.correo.remitente}") String remitente) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }
}