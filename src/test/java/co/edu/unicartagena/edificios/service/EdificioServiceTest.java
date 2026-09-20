package co.edu.unicartagena.edificios.service;

import co.edu.unicartagena.edificios.model.Edificio;
import co.edu.unicartagena.edificios.repository.EdificioRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class EdificioServiceTest {

    private final EdificioService servicio = new EdificioService(mock(EdificioRepository.class));

    private Edificio edificio(int pisos, boolean ascensor) {
        Edificio e = new Edificio();
        e.setNumPisos(pisos);
        e.setTieneAscensor(ascensor);
        return e;
    }

    @Test
    void edificioDeMasDeSeisPisosSinAscensorEsInvalido() {
        assertThrows(NegocioException.class, () -> servicio.validarReglas(edificio(10, false)));
    }

    @Test
    void edificioBajoSinAscensorEsValido() {
        assertDoesNotThrow(() -> servicio.validarReglas(edificio(4, false)));
    }

    @Test
    void reporteRechazaRangoDePisosInvertido() {
        assertThrows(NegocioException.class, () -> servicio.reportePorCiudadYPisos("Cartagena", 20, 5));
    }

    @Test
    void reporteRechazaValoresInvertidos() {
        assertThrows(NegocioException.class,
                () -> servicio.reportePorAdministracion(new BigDecimal("900"), new BigDecimal("100"), "", ""));
    }
}