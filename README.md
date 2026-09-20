# Ejercicio 13 - Edificio (Spring Boot MVC + Thymeleaf)

Aplicación web con **Spring Boot MVC, Thymeleaf y Spring Data JPA**, renderizada del lado del servidor.
Universidad de Cartagena · Ingeniería de Software · Desarrollo Web · Unidad 2.

- Estudiante: Miguel Lara · Código 7502510046 · Cuarto semestre

**No es una API RESTful:** los controladores son `@Controller` (no `@RestController`), no usan `@ResponseBody` ni devuelven JSON. Cada método carga datos en el `Model` y retorna el nombre de una plantilla Thymeleaf; Spring genera el HTML.

## Guía utilizada

No fue posible abrir la carpeta de guías del profesor (Drive pedía permisos). Se siguió el **plan de respaldo: estructura estándar de Spring MVC por capas**:

| Paquete `co.edu.unicartagena.edificios` | Contenido |
|---|---|
| `model` | Entidades JPA `Usuario` y `Edificio` (con Bean Validation) |
| `repository` | Interfaces `JpaRepository` con consultas derivadas y `@Query` |
| `service` | Lógica de negocio (`EdificioService`, `UsuarioService`, `AuthService`, `CorreoService`) |
| `controller` | Controladores MVC |
| `config` | `AuthInterceptor`, `WebConfig`, `PasswordConfig` |
| `dto` | `UsuarioForm` (formulario de usuario) |

Plantillas en `src/main/resources/templates/` (`edificios/`, `usuarios/`, `auth/`, `fragments/layout.html`).
Flujo: navegador → Controller → Service → Repository → base de datos → Service → Controller/Model → Thymeleaf → HTML.
Formularios con `th:object`/`th:field`, validación con `@Valid` + `BindingResult`, Post/Redirect/Get e inyección de dependencias por constructor.

## Versiones

Java 17 · Spring Boot 3.5.16 · Maven 3.9 · PostgreSQL 14+.

## Base de datos

```bash
createdb -U postgres edificios_spring
psql -U postgres -d edificios_spring -f db/schema.sql
psql -U postgres -d edificios_spring -f db/data.sql
```

JPA solo **valida** el esquema (`ddl-auto=validate`); el esquema lo crea `db/schema.sql`, coherente con las entidades.
**Usuario:** tiene solo `id`, `clave`, `nombre`, `rol`. El `id` es el **correo electrónico**: sirve como nombre de inicio de sesión y como destino de la recuperación de clave, sin agregar columnas.

## Variables de entorno (ver `.env.example`)

`DB_URL`, `DB_USER`, `DB_PASSWORD`, `SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`, `SMTP_AUTH`, `SMTP_FROM`, `SMTP_STARTTLS`, `SMTP_SSL`, `PORT`.

## Ejecución local

```bash
mvn package
java -jar target/edificio-spring-mvc-1.0.0.jar      # http://localhost:8080
```

## Usuarios de prueba

| Correo | Clave | Rol |
|---|---|---|
| admin@edificios.com | Admin123 | ADMIN (gestiona usuarios y edificios) |
| operador@edificios.com | Operador123 | OPERADOR (gestiona edificios) |
| consulta@edificios.com | Consulta123 | CONSULTA (solo lectura) |

## Reportes (consultas Spring Data JPA)

- Edificio 1: por ciudad y rango de pisos (método derivado `findByCiudadContainingIgnoreCaseAndNumPisosBetween...`).
- Edificio 2: por valor de administración con filtros de ascensor y zona social (`@Query` con parámetros opcionales).
- Usuario 1: por rol. Usuario 2: por texto en nombre o correo/dominio (`@Query`).

## Decisiones técnicas

- **Sesión:** `HttpSession` con un `HandlerInterceptor` (`AuthInterceptor`): sin sesión redirige a `/login`; `/usuarios/**` solo ADMIN; CONSULTA es de solo lectura. Se eligió por ser más fácil de explicar que Spring Security completo.
- **Claves con BCrypt** (`spring-security-crypto`): nunca en texto plano.
- **Recuperación de clave:** se genera una clave temporal aleatoria, se envía con `spring-boot-starter-mail` y solo entonces se guarda su hash. La respuesta es igual exista o no el correo.
- **Sin SQL concatenado:** solo repositorios JPA y consultas con parámetros.
- **Errores:** `error.html` y `@ControllerAdvice` para errores de negocio.

## Despliegue

`Dockerfile` (Java 17). Variables de entorno anteriores; cargar `db/schema.sql` y `db/data.sql` en la base de datos en la nube.
