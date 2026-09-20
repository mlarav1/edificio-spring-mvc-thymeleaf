package co.edu.unicartagena.edificios.repository;

import co.edu.unicartagena.edificios.model.Edificio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/** Repositorio de Edificio con los dos reportes parametrizados. */
public interface EdificioRepository extends JpaRepository<Edificio, Integer> {

    List<Edificio> findAllByOrderByCiudadAscNombreAsc();

    /** Reporte 1: por ciudad (parcial, sin mayusculas) y rango de pisos. Consulta derivada del nombre del metodo. */
    List<Edificio> findByCiudadContainingIgnoreCaseAndNumPisosBetweenOrderByNumPisosDescNombreAsc(
            String ciudad, Integer pisosMin, Integer pisosMax);

    /**
     * Reporte 2: rango de valor de administracion con filtros opcionales de ascensor y zona social.
     * Un parametro nulo significa "cualquiera".
     */
    @Query("SELECT e FROM Edificio e WHERE e.valorAdministracion BETWEEN :min AND :max "
            + "AND (:ascensor IS NULL OR e.tieneAscensor = :ascensor) "
            + "AND (:zona IS NULL OR e.tieneZonaSocial = :zona) "
            + "ORDER BY e.valorAdministracion DESC")
    List<Edificio> reportePorAdministracion(@Param("min") BigDecimal min, @Param("max") BigDecimal max,
                                            @Param("ascensor") Boolean ascensor, @Param("zona") Boolean zona);
}
