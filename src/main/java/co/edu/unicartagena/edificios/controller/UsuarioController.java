package co.edu.unicartagena.edificios.controller;

import co.edu.unicartagena.edificios.config.AuthInterceptor;
import co.edu.unicartagena.edificios.dto.UsuarioForm;
import co.edu.unicartagena.edificios.model.Usuario;
import co.edu.unicartagena.edificios.service.NegocioException;
import co.edu.unicartagena.edificios.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Controlador MVC de Usuario. Solo el ADMIN llega aqui (AuthInterceptor responde 403 al resto). */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) { this.servicio = servicio; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", servicio.listar());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        UsuarioForm f = new UsuarioForm();
        f.setNuevo(true);
        model.addAttribute("usuarioForm", f);
        return "usuarios/form";
    }

    @GetMapping("/editar")
    public String editar(@RequestParam String id, Model model) {
        Usuario u = servicio.buscar(id);
        UsuarioForm f = new UsuarioForm();
        f.setId(u.getId());
        f.setNombre(u.getNombre());
        f.setRol(u.getRol());
        model.addAttribute("usuarioForm", f);
        return "usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuarioForm") UsuarioForm form, BindingResult resultado,
                          RedirectAttributes flash) {
        if (!resultado.hasErrors()) {
            try {
                if (form.isNuevo()) servicio.crear(form); else servicio.actualizar(form);
                flash.addFlashAttribute("mensaje", form.isNuevo() ? "Usuario creado correctamente." : "Usuario actualizado correctamente.");
                flash.addFlashAttribute("tipo", "ok");
                return "redirect:/usuarios";         // Post/Redirect/Get
            } catch (NegocioException e) {
                resultado.reject("negocio", e.getMessage());
            }
        }
        return "usuarios/form";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam String id, HttpSession sesion, RedirectAttributes flash) {
        Usuario actual = (Usuario) sesion.getAttribute(AuthInterceptor.ATRIBUTO_USUARIO);
        try {
            servicio.eliminar(id, actual.getId());
            flash.addFlashAttribute("mensaje", "Usuario eliminado.");
            flash.addFlashAttribute("tipo", "ok");
        } catch (NegocioException e) {
            flash.addFlashAttribute("mensaje", e.getMessage());
            flash.addFlashAttribute("tipo", "error");
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/reportes")
    public String reportes() { return "usuarios/reportes"; }

    /** Reporte 1: por rol. */
    @GetMapping("/reportes/rol")
    public String reportePorRol(@RequestParam String rol, Model model) {
        try {
            model.addAttribute("resultado", servicio.reportePorRol(rol));
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("reporte", "1");
        return "usuarios/reportes";
    }

    /** Reporte 2: por texto en nombre o correo/dominio. */
    @GetMapping("/reportes/texto")
    public String reportePorTexto(@RequestParam String texto, Model model) {
        try {
            model.addAttribute("resultado", servicio.reportePorTexto(texto));
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("reporte", "2");
        return "usuarios/reportes";
    }
}
