package co.edu.unicartagena.edificios.service;

/** Error de regla de negocio; su mensaje se muestra al usuario. */
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) { super(mensaje); }
    public NegocioException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
