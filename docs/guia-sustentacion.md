# Guía de estudio para la sustentación (Spring Boot MVC + Thymeleaf, Ejercicio 13 - Edificio)

## 1. ¿Cómo está organizada la arquitectura por capas?

| Capa | Paquete | Clases |
|---|---|---|
| Controller | `controller` | `EdificioController`, `UsuarioController`, `AuthController`, `HomeController` |
| Service | `service` | `EdificioService`, `UsuarioService`, `AuthService`, `CorreoService` |
| Repository | `repository` | `EdificioRepository`, `UsuarioRepository` |
| Entity/Model | `model` | `Edificio`, `Usuario` |
| Vista | `resources/templates` | `edificios/*.html`, `usuarios/*.html`, `fragments/layout.html` |
| Configuración | `config` | `AuthInterceptor`, `WebConfig`, `PasswordConfig` |

## 2. ¿Cuál es la diferencia entre este enfoque y una API RESTful?

Aquí los controladores son `@Controller`: reciben la solicitud, cargan datos en el `Model` y **retornan el nombre de una plantilla** (por ejemplo `"edificios/lista"`). Thymeleaf genera el HTML en el servidor y el navegador recibe una página completa. En una API REST se usaría `@RestController`/`@ResponseBody`, se devolvería JSON y habría un frontend aparte; nada de eso existe en este proyecto (se puede comprobar con `grep -rE "RestController|ResponseBody" src/`).

## 3. ¿Qué es la inyección de dependencias y dónde se ve?

Spring crea los objetos (beans) y los entrega a quien los necesita. Se usa **inyección por constructor**: `EdificioController(EdificioService servicio)`, `EdificioService(EdificioRepository repositorio)`. Las clases anotadas con `@Controller`, `@Service` y `@Configuration` se detectan al arrancar (`@SpringBootApplication`). Ventaja: dependencias explícitas, campos `final` y pruebas fáciles (en `EdificioServiceTest` se pasa un repositorio simulado).

## 4. ¿Qué es un Repository y cómo se hacen los reportes?

Es una interfaz que extiende `JpaRepository`; Spring Data genera la implementación. Los reportes usan:
- un **método derivado del nombre**: `findByCiudadContainingIgnoreCaseAndNumPisosBetween...` (ciudad + rango de pisos);
- `@Query` con parámetros con nombre (`:min`, `:max`, `:ascensor`, `:zona`), donde un parámetro nulo significa "cualquiera".
Nunca se concatena SQL.

## 5. ¿Cómo funciona el binding de formularios?

En la plantilla `edificios/form.html`, `th:object="${edificio}"` enlaza el formulario con la entidad y `th:field="*{nombre}"` genera `name`, `id` y `value`. Al enviar, Spring convierte los parámetros en un objeto `Edificio` (`@ModelAttribute`), lo valida con `@Valid` (anotaciones `@NotBlank`, `@DecimalMin`, `@Min`... en la entidad) y deja los errores en `BindingResult`. Si hay errores se vuelve a mostrar el formulario con lo digitado y los mensajes junto a cada campo.

## 6. ¿Qué es Post/Redirect/Get?

Tras guardar o eliminar, el controlador retorna `redirect:/edificios` en vez de mostrar la página directamente. Así, si el usuario recarga, el navegador repite un GET y no reenvía el formulario. Los mensajes de éxito viajan con `RedirectAttributes.addFlashAttribute`.

## 7. ¿Cómo funciona una petición completa?

Guardar un edificio: navegador → `POST /edificios/guardar` → `AuthInterceptor` (sesión y rol) → `EdificioController.guardar` (binding + `@Valid`) → `EdificioService.guardar` (regla: más de 6 pisos exige ascensor) → `EdificioRepository.save` → PostgreSQL → redirect a `/edificios` → `EdificioController.listar` carga el `Model` → plantilla `edificios/lista.html` → HTML al navegador.

## 8. ¿Cómo se maneja la sesión y el control de acceso?

`AuthController.iniciarSesion` valida con `AuthService` (BCrypt), cambia el id de sesión (`changeSessionId`, evita fijación de sesión) y guarda el `Usuario` (sin clave) en `HttpSession`. `AuthInterceptor` (registrado en `WebConfig`) corre antes de cada controlador: sin sesión redirige a `/login`; `/usuarios/**` solo ADMIN; crear/editar/eliminar edificios solo ADMIN u OPERADOR (CONSULTA recibe 403). Se eligió un interceptor y no Spring Security completo por ser más fácil de explicar.

## 9. ¿Cómo se guardan las claves y cómo se recupera una clave?

Con BCrypt (`PasswordEncoder`); nunca en texto plano. `AuthService.solicitarRecuperacion` genera un token de 256 bits con `SecureRandom`, guarda solo su hash SHA-256 con vencimiento de 30 minutos (tabla `token_recuperacion`) y `CorreoService` envía por correo (API de Brevo o SMTP) el enlace `/restablecer?token=...`. `AuthService.restablecer` valida el token, guarda la nueva clave con BCrypt y borra el token: solo sirve una vez. La respuesta es igual exista o no el correo, para no revelar usuarios.

## 10. ¿Por qué el `id` del Usuario es el correo?

El enunciado limita Usuario a `id`, `clave`, `nombre` y `rol`. Con el correo como `id` sirve para iniciar sesión y recuperar la clave sin columnas extra.

## 11. ¿Por qué `ddl-auto=validate`?

El esquema lo crea `db/schema.sql` (con restricciones `CHECK`); JPA solo comprueba que las entidades coincidan con las tablas. Así el esquema queda bajo control y es igual en local y en producción.

## 12. ¿Qué pasa cuando hay un error?

Los errores de negocio son `NegocioException`; el controlador los convierte en mensajes o `@ControllerAdvice` (`GlobalModelAdvice`) muestra `error.html`. Los códigos 403/404/500 también usan `error.html`.
