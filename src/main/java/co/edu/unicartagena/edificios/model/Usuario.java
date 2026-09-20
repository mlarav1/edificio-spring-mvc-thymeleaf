package co.edu.unicartagena.edificios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Usuario: exactamente cuatro atributos.
 * El "id" es el correo electronico (inicio de sesion y destino de la recuperacion de clave).
 */
@Entity
@Table(name = "usuario")
public class Usuario {
    public static final String ADMIN = "ADMIN";
    public static final String OPERADOR = "OPERADOR";
    public static final String CONSULTA = "CONSULTA";

    @Id
    @Column(length = 120)
    private String id;

    @Column(nullable = false, length = 100)
    private String clave;      // hash BCrypt

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String rol;

    public Usuario() { }

    public Usuario(String id, String clave, String nombre, String rol) {
        this.id = id; this.clave = clave; this.nombre = nombre; this.rol = rol;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean esAdmin() { return ADMIN.equals(rol); }
    /** ADMIN y OPERADOR modifican edificios; CONSULTA solo lee. */
    public boolean puedeEscribir() { return ADMIN.equals(rol) || OPERADOR.equals(rol); }
}
