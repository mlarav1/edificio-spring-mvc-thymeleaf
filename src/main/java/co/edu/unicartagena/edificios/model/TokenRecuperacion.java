package co.edu.unicartagena.edificios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Token de recuperacion de clave. Solo se guarda el hash SHA-256 del token (el token en
 * claro viaja unicamente en el enlace del correo). Es una entidad aparte para que
 * Usuario conserve exactamente sus cuatro atributos.
 */
@Entity
@Table(name = "token_recuperacion")
public class TokenRecuperacion {

    @Id
    @Column(name = "token_hash", length = 64)
    private String tokenHash;

    @Column(name = "usuario_id", nullable = false, length = 120)
    private String usuarioId;

    @Column(nullable = false)
    private LocalDateTime expira;

    public TokenRecuperacion() { }

    public TokenRecuperacion(String tokenHash, String usuarioId, LocalDateTime expira) {
        this.tokenHash = tokenHash;
        this.usuarioId = usuarioId;
        this.expira = expira;
    }

    public String getTokenHash() { return tokenHash; }
    public String getUsuarioId() { return usuarioId; }
    public LocalDateTime getExpira() { return expira; }
    public boolean vencido() { return expira.isBefore(LocalDateTime.now()); }
}
