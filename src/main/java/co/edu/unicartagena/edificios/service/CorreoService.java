package co.edu.unicartagena.edificios.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/** Envio de correo con spring-boot-starter-mail; el SMTP se configura por variables de entorno. */
@Service
public class CorreoService {

    private final JavaMailSender enviador;
    private final String remitente;

    public CorreoService(JavaMailSender enviador, @Value("${app.mail.from}") String remitente) {
        this.enviador = enviador;
        this.remitente = remitente;
    }

    public void enviar(String destino, String asunto, String texto) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(remitente);
        m.setTo(destino);
        m.setSubject(asunto);
        m.setText(texto);
        try {
            enviador.send(m);
        } catch (MailException e) {
            throw new NegocioException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }
}
