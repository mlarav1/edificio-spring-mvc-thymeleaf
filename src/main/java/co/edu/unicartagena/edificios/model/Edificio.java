package co.edu.unicartagena.edificios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Entidad Edificio (ejercicio 13). Las anotaciones de Bean Validation se comprueban
 * con @Valid en el controlador antes de guardar.
 */
@Entity
@Table(name = "edificio")
public class Edificio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres.")
    private String nombre;

    @NotNull(message = "Los metros cuadrados son obligatorios.")
    @DecimalMin(value = "0.01", message = "Los metros cuadrados deben ser mayores que cero.")
    @Column(precision = 12, scale = 2)
    private BigDecimal metrosCuadrados;

    @NotNull(message = "La altura es obligatoria.")
    @DecimalMin(value = "0.01", message = "La altura debe ser mayor que cero.")
    @Column(precision = 8, scale = 2)
    private BigDecimal altura;

    @NotNull(message = "El número de pisos es obligatorio.")
    @Min(value = 1, message = "El número de pisos debe ser al menos 1.")
    private Integer numPisos;

    @NotNull @Min(value = 0, message = "Los apartamentos no pueden ser negativos.")
    private Integer numApartamentos = 0;

    @NotNull @Min(value = 0, message = "Las oficinas no pueden ser negativas.")
    private Integer numOficinas = 0;

    private String nombreParqueadero;

    @NotNull @Min(value = 0, message = "Las piscinas no pueden ser negativas.")
    private Integer numPiscinas = 0;

    @NotBlank(message = "El país es obligatorio.")
    private String pais = "Colombia";

    @NotBlank(message = "El departamento es obligatorio.")
    private String departamento;

    @NotBlank(message = "La ciudad es obligatoria.")
    private String ciudad;

    private Boolean tieneAscensor = false;

    @NotNull(message = "El valor de administración es obligatorio.")
    @DecimalMin(value = "0", message = "El valor de administración no puede ser negativo.")
    @Column(precision = 14, scale = 2)
    private BigDecimal valorAdministracion;

    private Boolean tieneZonaSocial = false;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getMetrosCuadrados() { return metrosCuadrados; }
    public void setMetrosCuadrados(BigDecimal v) { this.metrosCuadrados = v; }
    public BigDecimal getAltura() { return altura; }
    public void setAltura(BigDecimal altura) { this.altura = altura; }
    public Integer getNumPisos() { return numPisos; }
    public void setNumPisos(Integer numPisos) { this.numPisos = numPisos; }
    public Integer getNumApartamentos() { return numApartamentos; }
    public void setNumApartamentos(Integer v) { this.numApartamentos = v; }
    public Integer getNumOficinas() { return numOficinas; }
    public void setNumOficinas(Integer v) { this.numOficinas = v; }
    public String getNombreParqueadero() { return nombreParqueadero; }
    public void setNombreParqueadero(String v) { this.nombreParqueadero = v; }
    public Integer getNumPiscinas() { return numPiscinas; }
    public void setNumPiscinas(Integer v) { this.numPiscinas = v; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String v) { this.departamento = v; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public Boolean getTieneAscensor() { return tieneAscensor; }
    public void setTieneAscensor(Boolean v) { this.tieneAscensor = v; }
    public BigDecimal getValorAdministracion() { return valorAdministracion; }
    public void setValorAdministracion(BigDecimal v) { this.valorAdministracion = v; }
    public Boolean getTieneZonaSocial() { return tieneZonaSocial; }
    public void setTieneZonaSocial(Boolean v) { this.tieneZonaSocial = v; }
}
