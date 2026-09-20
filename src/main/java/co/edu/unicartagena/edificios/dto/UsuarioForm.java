package co.edu.unicartagena.edificios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos del formulario de usuario (la clave en texto plano solo vive aqui, nunca en la entidad). */
public class UsuarioForm {
    private boolean nuevo;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Escribe un correo electrónico válido.")
    private String id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @Pattern(regexp = "ADMIN|OPERADOR|CONSULTA", message = "El rol debe ser ADMIN, OPERADOR o CONSULTA.")
    private String rol = "CONSULTA";

    private String clave;

    public boolean isNuevo() { return nuevo; }
    public void setNuevo(boolean nuevo) { this.nuevo = nuevo; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
}
