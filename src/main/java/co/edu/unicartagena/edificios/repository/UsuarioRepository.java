package co.edu.unicartagena.edificios.repository;

import co.edu.unicartagena.edificios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Usuario. Spring Data genera la implementacion en tiempo de ejecucion;
 * las consultas de reporte usan parametros con nombre (nunca se concatena SQL).
 */
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByIdIgnoreCase(String id);

    long countByRol(String rol);

    /** Reporte 1: usuarios de un rol (metodo derivado del nombre). */
    List<Usuario> findByRolOrderByNombreAsc(String rol);

    /** Reporte 2: texto contenido en el nombre o en el correo/dominio. */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR LOWER(u.id) LIKE LOWER(CONCAT('%', :texto, '%')) ORDER BY u.nombre")
    List<Usuario> buscarPorTexto(@Param("texto") String texto);
}
