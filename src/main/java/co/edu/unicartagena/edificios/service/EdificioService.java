package co.edu.unicartagena.edificios.service;

import co.edu.unicartagena.edificios.model.Edificio;
import co.edu.unicartagena.edificios.repository.EdificioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Logica de negocio de Edificio. El repositorio llega por inyeccion de dependencias
 * en el constructor (no se usa @Autowired en campos).
 */
@Service
public class EdificioService {

    private final EdificioRepository repositorio;

    public EdificioService(EdificioRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Edificio> listar() { return repositorio.findAllByOrderByCiudadAscNombreAsc(); }

    public Edificio buscar(Integer id) {
        return repositorio.findById(id).orElseThrow(() -> new NegocioException("El edificio no existe."));
    }

    /** Regla de negocio que Bean Validation no cubre: un edificio de mas de 6 pisos necesita ascensor. */
    public void validarReglas(Edificio e) {
        if (e.getNumPisos() != null && e.getNumPisos() > 6 && !Boolean.TRUE.equals(e.getTieneAscensor()))
            throw new NegocioException("Un edificio de más de 6 pisos debe tener ascensor.");
    }

    public Edificio guardar(Edificio e) {
        validarReglas(e);
        return repositorio.save(e);
    }

    public void eliminar(Integer id) {
        if (!repositorio.existsById(id)) throw new NegocioException("El edificio no existe.");
        repositorio.deleteById(id);
    }

    public List<Edificio> reportePorCiudadYPisos(String ciudad, Integer min, Integer max) {
        if (ciudad == null || ciudad.isBlank()) throw new NegocioException("Escribe la ciudad.");
        if (min == null || max == null) throw new NegocioException("Indica los pisos mínimo y máximo.");
        if (min > max) throw new NegocioException("Los pisos mínimos no pueden superar a los máximos.");
        return repositorio.findByCiudadContainingIgnoreCaseAndNumPisosBetweenOrderByNumPisosDescNombreAsc(
                ciudad.trim(), min, max);
    }

    public List<Edificio> reportePorAdministracion(BigDecimal min, BigDecimal max, String ascensor, String zona) {
        if (min == null || max == null) throw new NegocioException("Indica el valor mínimo y el máximo.");
        if (min.compareTo(max) > 0) throw new NegocioException("El valor mínimo no puede superar al máximo.");
        return repositorio.reportePorAdministracion(min, max, filtro(ascensor), filtro(zona));
    }

    /** "SI" -> true, "NO" -> false, cualquier otro valor -> null (sin filtrar). */
    private Boolean filtro(String v) {
        if ("SI".equals(v)) return true;
        if ("NO".equals(v)) return false;
        return null;
    }
}
