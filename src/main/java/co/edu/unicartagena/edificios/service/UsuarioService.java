package co.edu.unicartagena.edificios.service;

import co.edu.unicartagena.edificios.dto.UsuarioForm;
import co.edu.unicartagena.edificios.model.Usuario;
import co.edu.unicartagena.edificios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/** Logica de negocio de Usuario: unicidad del correo, hash de clave y reglas de rol. */
@Service
public class UsuarioService {

    private final UsuarioRepository repositorio;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repositorio, PasswordEncoder encoder) {
        this.repositorio = repositorio;
        this.encoder = encoder;
    }

    public List<Usuario> listar() { return repositorio.findAll(org.springframework.data.domain.Sort.by("nombre")); }

    public Usuario buscar(String id) {
        return repositorio.findByIdIgnoreCase(id).orElseThrow(() -> new NegocioException("El usuario no existe."));
    }

    public void crear(UsuarioForm f) {
        String correo = f.getId().trim().toLowerCase();
        if (repositorio.findByIdIgnoreCase(correo).isPresent())
            throw new NegocioException("Ya existe un usuario con el correo " + correo + ".");
        validarClave(f.getClave());
        repositorio.save(new Usuario(correo, encoder.encode(f.getClave()), f.getNombre().trim(), f.getRol()));
    }

    public void actualizar(UsuarioForm f) {
        Usuario u = buscar(f.getId());
        // Regla: siempre debe quedar al menos un administrador.
        if (u.esAdmin() && !Usuario.ADMIN.equals(f.getRol()) && repositorio.countByRol(Usuario.ADMIN) <= 1)
            throw new NegocioException("Debe existir al menos un administrador.");
        u.setNombre(f.getNombre().trim());
        u.setRol(f.getRol());
        if (f.getClave() != null && !f.getClave().isBlank()) {
            validarClave(f.getClave());
            u.setClave(encoder.encode(f.getClave()));
        }
        repositorio.save(u);
    }

    public void eliminar(String id, String idSesion) {
        if (id.equalsIgnoreCase(idSesion))
            throw new NegocioException("No puedes eliminar tu propio usuario mientras tienes la sesión abierta.");
        Usuario u = buscar(id);
        if (u.esAdmin() && repositorio.countByRol(Usuario.ADMIN) <= 1)
            throw new NegocioException("Debe existir al menos un administrador.");
        repositorio.delete(u);
    }

    public List<Usuario> reportePorRol(String rol) {
        if (rol == null || rol.isBlank()) throw new NegocioException("Selecciona un rol.");
        return repositorio.findByRolOrderByNombreAsc(rol);
    }

    public List<Usuario> reportePorTexto(String texto) {
        if (texto == null || texto.trim().length() < 2)
            throw new NegocioException("Escribe al menos 2 caracteres para buscar.");
        return repositorio.buscarPorTexto(texto.trim());
    }

    private void validarClave(String clave) {
        if (clave == null || clave.length() < 6)
            throw new NegocioException("La clave debe tener al menos 6 caracteres.");
    }
}
