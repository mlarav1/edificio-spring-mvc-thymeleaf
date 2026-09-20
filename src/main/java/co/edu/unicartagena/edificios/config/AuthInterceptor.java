package co.edu.unicartagena.edificios.config;

import co.edu.unicartagena.edificios.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Verificacion de sesion y de rol antes de CADA controlador.
 * - Sin sesion se redirige a /login (las rutas publicas se excluyen en WebConfig).
 * - /usuarios/** solo para ADMIN.
 * - Crear, editar y eliminar edificios: ADMIN u OPERADOR (CONSULTA solo lee).
 */
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_USUARIO = "usuario";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion == null) ? null : (Usuario) sesion.getAttribute(ATRIBUTO_USUARIO);
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login?sesion");
            return false;
        }
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        if (ruta.startsWith("/usuarios") && !usuario.esAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        boolean modifica = ruta.startsWith("/edificios/nuevo") || ruta.startsWith("/edificios/editar")
                || ruta.startsWith("/edificios/guardar") || ruta.startsWith("/edificios/eliminar");
        if (modifica && !usuario.puedeEscribir()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        // Evita que el navegador guarde paginas privadas.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        return true;
    }
}
