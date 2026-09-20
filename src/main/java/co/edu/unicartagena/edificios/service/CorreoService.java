package co.edu.unicartagena.edificios.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.HtmlUtils;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Envio del correo de recuperacion. Usa el primer medio configurado:
 *  1. API HTTPS de Brevo (BREVO_API_KEY): es el medio para la nube, porque el plan
 *     gratuito de Render bloquea los puertos SMTP.
 *  2. SMTP con JavaMailSender (SMTP_HOST).
 *  3. Sin ninguno (desarrollo local): el enlace se escribe en el log del servidor.
 * Las claves nunca estan en el codigo: se leen de variables de entorno.
 */
@Service
public class CorreoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoService.class);
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String ASUNTO = "Recuperación de clave - Edificios";

    private final JavaMailSender smtp;
    private final RestClient http;
    private final String brevoApiKey;
    private final String smtpHost;
    private final String remitente;
    private final String nombreRemitente;
    private final int minutosVigencia;

    public CorreoService(JavaMailSender smtp,
                         @Value("${app.mail.brevo-api-key:}") String brevoApiKey,
                         @Value("${app.mail.smtp-host:}") String smtpHost,
                         @Value("${app.mail.from}") String remitente,
                         @Value("${app.mail.from-name}") String nombreRemitente,
                         @Value("${app.reset-token-minutes}") int minutosVigencia) {
        this.smtp = smtp;
        this.brevoApiKey = brevoApiKey;
        this.smtpHost = smtpHost;
        this.remitente = remitente;
        this.nombreRemitente = nombreRemitente;
        this.minutosVigencia = minutosVigencia;
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
        fabrica.setReadTimeout(Duration.ofSeconds(15));
        this.http = RestClient.builder().requestFactory(fabrica).build();
    }

    /** @return true si el correo salio por Brevo o SMTP; false si solo quedo en el log. */
    public boolean enviarRecuperacion(String destino, String nombre, String enlace) {
        String texto = "Hola " + nombre + ",\n\nPara restablecer tu clave abre este enlace (vigente "
                + minutosVigencia + " minutos y de un solo uso):\n" + enlace
                + "\n\nSi no lo solicitaste, ignora este mensaje.\n";
        String html = "<p>Hola <strong>" + HtmlUtils.htmlEscape(nombre) + "</strong>,</p>"
                + "<p>Recibimos una solicitud para restablecer tu clave en <strong>Edificios</strong>.</p>"
                + "<p><a href=\"" + HtmlUtils.htmlEscape(enlace) + "\">Restablecer mi clave</a></p>"
                + "<p>El enlace vence en " + minutosVigencia + " minutos y solo se puede usar una vez.</p>"
                + "<p>Si no lo solicitaste, ignora este mensaje.</p>";
        try {
            if (!brevoApiKey.isBlank()) {
                enviarPorBrevo(destino, nombre, html, texto);
                log.info("Correo de recuperación enviado por Brevo a {}", destino);
                return true;
            }
            if (!smtpHost.isBlank()) {
                enviarPorSmtp(destino, html, texto);
                log.info("Correo de recuperación enviado por SMTP a {}", destino);
                return true;
            }
            log.warn("Correo no configurado (falta BREVO_API_KEY o SMTP_HOST).");
        } catch (Exception e) {
            log.error("No se pudo enviar el correo de recuperación a {}: {}", destino, e.getMessage());
        }
        // Modo desarrollo o fallo de envio: el enlace queda solo en el log del servidor.
        log.info("Enlace de recuperación para {}: {}", destino, enlace);
        return false;
    }

    private void enviarPorBrevo(String destino, String nombre, String html, String texto) {
        Map<String, Object> cuerpo = Map.of(
                "sender", Map.of("name", nombreRemitente, "email", remitente),
                "to", List.of(Map.of("email", destino, "name", nombre)),
                "subject", ASUNTO,
                "htmlContent", html,
                "textContent", texto);
        http.post().uri(BREVO_URL)
                .header("api-key", brevoApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(cuerpo)
                .retrieve()
                .toBodilessEntity();
    }

    private void enviarPorSmtp(String destino, String html, String texto) throws Exception {
        MimeMessage m = smtp.createMimeMessage();
        MimeMessageHelper h = new MimeMessageHelper(m, true, StandardCharsets.UTF_8.name());
        h.setFrom(remitente, nombreRemitente);
        h.setTo(destino);
        h.setSubject(ASUNTO);
        h.setText(texto, html);
        smtp.send(m);
    }
}
