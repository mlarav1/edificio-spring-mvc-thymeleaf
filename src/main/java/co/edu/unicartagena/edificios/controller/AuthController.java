package co.edu.unicartagena.edificios.controller;

import co.edu.unicartagena.edificios.config.AuthInterceptor;
import co.edu.unicartagena.edificios.model.Usuario;
import co.edu.unicartagena.edificios.service.AuthService;
import co.edu.unicartagena.edificios.service.NegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Login, cierre de sesion y recuperacion de clave. Retorna nombres de plantillas Thymeleaf. */
@Controller
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @GetMapping("/login")
    public String login(HttpSession sesion, Model model, @RequestParam(required = false) String sesionExpirada) {
        if (sesion.getAttribute(AuthInterceptor.ATRIBUTO_USUARIO) != null) return "redirect:/";
        return "auth/login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String correo, @RequestParam String clave,
                                HttpServletRequest request, RedirectAttributes flash) {
        try {
            Usuario u = auth.autenticar(correo, clave);
            request.getSession(true);
            request.changeSessionId();          // nueva sesion al autenticar (evita fijacion de sesion)
            u.setClave(null);                   // la clave no viaja en la sesion
            request.getSession().setAttribute(AuthInterceptor.ATRIBUTO_USUARIO, u);
            return "redirect:/";
        } catch (NegocioException e) {
            flash.addFlashAttribute("mensaje", e.getMessage());
            flash.addFlashAttribute("tipo", "error");
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession sesion, RedirectAttributes flash) {
        sesion.invalidate();
        flash.addFlashAttribute("mensaje", "Cerraste sesión correctamente.");
        flash.addFlashAttribute("tipo", "ok");
        return "redirect:/login";
    }

    @GetMapping("/recuperar")
    public String recuperar() { return "auth/recuperar"; }

    @PostMapping("/recuperar")
    public String enviarClave(@RequestParam String correo, RedirectAttributes flash) {
        try {
            auth.recuperarClave(correo);
            // Respuesta identica exista o no el correo.
            flash.addFlashAttribute("mensaje", "Si el correo existe, te enviamos una clave temporal.");
            flash.addFlashAttribute("tipo", "ok");
            return "redirect:/login";
        } catch (NegocioException e) {
            flash.addFlashAttribute("mensaje", e.getMessage());
            flash.addFlashAttribute("tipo", "error");
            return "redirect:/recuperar";
        }
    }
}
