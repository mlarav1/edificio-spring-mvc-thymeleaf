package co.edu.unicartagena.edificios.repository;

import co.edu.unicartagena.edificios.model.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, String> {

    /** Un usuario solo tiene un enlace vigente: al pedir otro se borran los anteriores. */
    @Modifying
    @Transactional
    @Query("DELETE FROM TokenRecuperacion t WHERE t.usuarioId = :usuarioId")
    void borrarDeUsuario(@Param("usuarioId") String usuarioId);
}
