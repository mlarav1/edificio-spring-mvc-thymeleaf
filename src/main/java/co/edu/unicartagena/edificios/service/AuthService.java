package co.edu.unicartagena.edificios.service;

import co.edu.unicartagena.edificios.model.TokenRecuperacion;
import co.edu.unicartagena.edificios.model.Usuario;
import co.edu.unicartagena.edificios.repository.TokenRecuperacionRepository;
import co.edu.unicartagena.edificios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

/** Autenticacion y recuperacion de clave con token de un solo uso. */
@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final TokenRecuperacionRepository tokens;
    private final PasswordEncoder encoder;
    private final CorreoService correo;
    private final String baseUrl;
    private final int minutosVigencia;

    public AuthService(UsuarioRepository usuarios, TokenRecuperacionRepository tokens, PasswordEncoder encoder,
                       CorreoService correo, @Value("${app.base-url}") String baseUrl,
                       @Value("${app.reset-token-minutes}") int minutosVigencia) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.encoder = encoder;
        this.correo = correo;
        this.baseUrl = baseUrl;
        this.minutosVigencia = minutosVigencia;
    }

    /** Devuelve el usuario si correo y clave son correctos; si no lanza NegocioException. */
    public Usuario autenticar(String correoUsuario, String clave) {
        if (correoUsuario == null || correoUsuario.isBlank() || clave == null || clave.isEmpty())
            throw new NegocioException("Ingresa tu correo y tu clave.");
        // Mismo mensaje si no existe o si la clave es incorrecta: no revela que correos existen.
        Usuario u = usuarios.findByIdIgnoreCase(correoUsuario.trim()).orElse(null);
        if (u == null || !encoder.matches(clave, u.getClave()))
            throw new NegocioException("Correo o clave incorrectos.");
        return u;
    }

    /**
     * Genera un token aleatorio, guarda SOLO su hash SHA-256 con vencimiento y envia por correo
     * el enlace /restablecer?token=... Si el correo no existe no se informa (evita enumerar usuarios).
     */
    @Transactional
    public void solicitarRecuperacion(String correoUsuario) {
        if (correoUsuario == null || correoUsuario.isBlank()) throw new NegocioException("Ingresa tu correo.");
        Usuario u = usuarios.findByIdIgnoreCase(correoUsuario.trim()).orElse(null);
        if (u == null) return;
        String token = generarToken();
        tokens.borrarDeUsuario(u.getId());
        tokens.save(new TokenRecuperacion(hash(token), u.getId(), LocalDateTime.now().plusMinutes(minutosVigencia)));
        correo.enviarRecuperacion(u.getId(), u.getNombre(),
                baseUrl.replaceAll("/+$", "") + "/restablecer?token=" + token);
    }

    /** Valida que el token exista y no haya vencido; devuelve el usuario dueño del token. */
    public Usuario validarToken(String token) {
        if (token == null || token.isBlank()) throw new NegocioException("El enlace de recuperación no es válido.");
        TokenRecuperacion t = tokens.findById(hash(token.trim()))
                .orElseThrow(() -> new NegocioException("El enlace es inválido o ya fue utilizado."));
        if (t.vencido()) {
            tokens.delete(t);
            throw new NegocioException("El enlace venció. Solicita uno nuevo.");
        }
        return usuarios.findById(t.getUsuarioId()).orElseThrow(() -> new NegocioException("El usuario no existe."));
    }

    /** Cambia la clave (guardada con BCrypt) y consume el token: solo sirve una vez. */
    @Transactional
    public void restablecer(String token, String nueva, String confirmacion) {
        if (nueva == null || nueva.length() < 6) throw new NegocioException("La clave debe tener al menos 6 caracteres.");
        if (!nueva.equals(confirmacion)) throw new NegocioException("Las claves no coinciden.");
        Usuario u = validarToken(token);
        u.setClave(encoder.encode(nueva));
        usuarios.save(u);
        tokens.borrarDeUsuario(u.getId());
    }

    /** Token de 256 bits en Base64 URL (sirve directo en el enlace). */
    static String generarToken() {
        byte[] b = new byte[32];
        new SecureRandom().nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    /** Hash SHA-256 en hexadecimal (64 caracteres). */
    static String hash(String token) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
