package co.edu.unicartagena.edificios.controller;

import co.edu.unicartagena.edificios.model.Edificio;
import co.edu.unicartagena.edificios.service.EdificioService;
import co.edu.unicartagena.edificios.service.NegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Controlador MVC de Edificio (@Controller, no @RestController): carga datos en el Model
 * y RETORNA EL NOMBRE de una plantilla Thymeleaf; Spring genera el HTML.
 */
@Controller
@RequestMapping("/edificios")
public class EdificioController {

    private final EdificioService servicio;

    public EdificioController(EdificioService servicio) { this.servicio = servicio; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("edificios", servicio.listar());
        return "edificios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("edificio", new Edificio());
        return "edificios/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("edificio", servicio.buscar(id));
        return "edificios/form";
    }

    /** th:object/th:field envian el formulario; @Valid + BindingResult aplican Bean Validation. */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("edificio") Edificio edificio, BindingResult resultado,
                          RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                boolean nuevo = edificio.getId() == null;
                servicio.guardar(edificio);
                flash.addFlashAttribute("mensaje", nuevo ? "Edificio creado correctamente." : "Edificio actualizado correctamente.");
                flash.addFlashAttribute("tipo", "ok");
                return "redirect:/edificios";        // Post/Redirect/Get
            } catch (NegocioException e) {
                resultado.reject("negocio", e.getMessage());
            }
        }
        return "edificios/form";                     // vuelve al formulario conservando lo digitado
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        servicio.eliminar(id);
        flash.addFlashAttribute("mensaje", "Edificio eliminado.");
        flash.addFlashAttribute("tipo", "ok");
        return "redirect:/edificios";
    }

    @GetMapping("/reportes")
    public String reportes() { return "edificios/reportes"; }

    /** Reporte 1: por ciudad y rango de pisos. */
    @GetMapping("/reportes/ciudad-pisos")
    public String reporteCiudadPisos(@RequestParam String ciudad, @RequestParam Integer pisosMin,
                                     @RequestParam Integer pisosMax, Model model) {
        try {
            model.addAttribute("resultado", servicio.reportePorCiudadYPisos(ciudad, pisosMin, pisosMax));
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("reporte", "1");
        return "edificios/reportes";
    }

    /** Reporte 2: por valor de administracion, ascensor y zona social. */
    @GetMapping("/reportes/administracion")
    public String reporteAdministracion(@RequestParam BigDecimal valorMin, @RequestParam BigDecimal valorMax,
                                        @RequestParam(defaultValue = "") String ascensor,
                                        @RequestParam(defaultValue = "") String zonaSocial, Model model) {
        try {
            model.addAttribute("resultado", servicio.reportePorAdministracion(valorMin, valorMax, ascensor, zonaSocial));
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("reporte", "2");
        return "edificios/reportes";
    }
}
