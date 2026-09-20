package co.edu.unicartagena.edificios.service;

import co.edu.unicartagena.edificios.model.Usuario;
import co.edu.unicartagena.edificios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/** Autenticacion y recuperacion de clave. */
@Service
public class AuthService {
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    private final UsuarioRepository repositorio;
    private final PasswordEncoder encoder;
    private final CorreoService correo;

    public AuthService(UsuarioRepository repositorio, PasswordEncoder encoder, CorreoService correo) {
        this.repositorio = repositorio;
        this.encoder = encoder;
        this.correo = correo;
    }

    /** Devuelve el usuario si correo y clave son correctos; si no lanza NegocioException. */
    public Usuario autenticar(String correoUsuario, String clave) {
        if (correoUsuario == null || correoUsuario.isBlank() || clave == null || clave.isEmpty())
            throw new NegocioException("Ingresa tu correo y tu clave.");
        // Mismo mensaje si no existe o si la clave es incorrecta: no revela que correos existen.
        Usuario u = repositorio.findByIdIgnoreCase(correoUsuario.trim()).orElse(null);
        if (u == null || !encoder.matches(clave, u.getClave()))
            throw new NegocioException("Correo o clave incorrectos.");
        return u;
    }

    /**
     * Genera una clave temporal, la envia por correo y solo entonces guarda su hash.
     * Si el correo no existe no se informa (evita enumerar usuarios).
     */
    public void recuperarClave(String correoUsuario) {
        if (correoUsuario == null || correoUsuario.isBlank()) throw new NegocioException("Ingresa tu correo.");
        Usuario u = repositorio.findByIdIgnoreCase(correoUsuario.trim()).orElse(null);
        if (u == null) return;
        String temporal = generarClave(10);
        correo.enviar(u.getId(), "Recuperación de clave - Edificios",
                "Hola " + u.getNombre() + ",\n\nTu clave temporal es: " + temporal
                        + "\n\nInicia sesión con ella y cámbiala cuanto antes.\n");
        u.setClave(encoder.encode(temporal));
        repositorio.save(u);
    }

    static String generarClave(int largo) {
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < largo; i++) sb.append(ALFABETO.charAt(r.nextInt(ALFABETO.length())));
        return sb.toString();
    }
}
