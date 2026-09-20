package co.edu.unicartagena.edificios.controller;

import co.edu.unicartagena.edificios.config.AuthInterceptor;
import co.edu.unicartagena.edificios.model.Usuario;
import co.edu.unicartagena.edificios.service.NegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Datos comunes a todas las plantillas (usuario de la sesion y ruta activa) y manejo de errores de negocio. */
@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(HttpSession sesion) {
        return (Usuario) sesion.getAttribute(AuthInterceptor.ATRIBUTO_USUARIO);
    }

    @ModelAttribute("ruta")
    public String ruta(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    /** Un error de negocio no controlado se muestra en la pagina de error amigable. */
    @ExceptionHandler(NegocioException.class)
    public String negocio(NegocioException e, Model model) {
        model.addAttribute("titulo", "No se pudo completar la operación");
        model.addAttribute("detalle", e.getMessage());
        return "error";
    }
}
