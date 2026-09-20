# Guion de sustentación (Spring Boot MVC + Thymeleaf, Ejercicio 13 - Edificio)

Duración aproximada: 11 a 13 minutos. Para leer en cámara.

## 1. Presentación (0:00 - 0:50)
"Hola, soy Miguel Lara, código 7502510046, cuarto semestre de Ingeniería de Software en la Universidad de Cartagena. Este es mi trabajo de Desarrollo Web, Unidad 2: una aplicación con Spring Boot MVC y Thymeleaf para el ejercicio 13, Edificio. Permite administrar edificios y usuarios, consultar reportes y controlar el acceso por roles. Es el mismo problema que resolví en la Unidad 1 con Servlets y JSP, ahora con un framework."

## 2. Estructura del proyecto y guía utilizada (0:50 - 2:00)
"No pude abrir la carpeta de guías del profesor, así que seguí la estructura estándar de Spring MVC por capas. Bajo el paquete `co.edu.unicartagena.edificios` tengo `model`, `repository`, `service`, `controller` y `config`. Las plantillas están en `resources/templates`, con un fragmento compartido `layout.html` para la cabecera y el menú."

## 3. Controller, Service, Repository, Entity y Thymeleaf (2:00 - 4:30)
"La entidad `Edificio` está en `model`, con anotaciones JPA y de validación. El repositorio `EdificioRepository` es una interfaz que extiende `JpaRepository`; Spring genera su implementación. El servicio `EdificioService` contiene la lógica de negocio. El controlador `EdificioController` recibe la solicitud, carga datos en el `Model` y retorna el nombre de una plantilla, por ejemplo `edificios/lista`, que Thymeleaf convierte en HTML. Todo se conecta por inyección de dependencias en el constructor."

## 4. Diferencia con una API RESTful (4:30 - 5:15)
"Esto no es una API REST: mis controladores son `@Controller`, no `@RestController`, no devuelven JSON y no hay un frontend aparte. Aquí Spring genera el HTML con Thymeleaf y el navegador recibe la página completa."

## 5. Recorrido de una operación completa (5:15 - 6:45)
"Voy a crear un edificio. El formulario usa `th:object` y `th:field` para enlazarse con la entidad. Al enviarlo, el interceptor verifica mi sesión y mi rol; el controlador valida con `@Valid`; el servicio aplica la regla de que más de seis pisos exige ascensor; el repositorio guarda en PostgreSQL. Después el controlador redirige al listado: es el patrón Post/Redirect/Get. Si dejo un dato inválido, el formulario vuelve con el mensaje junto al campo."

## 6. Login y control de acceso (6:45 - 7:45)
"Inicio sesión con mi correo y mi clave; se comparan con BCrypt y el usuario se guarda en la sesión. El `AuthInterceptor` corre antes de cada controlador: sin sesión me redirige al login, solo el administrador entra a usuarios, y el rol consulta no puede crear ni editar edificios; recibe acceso denegado."

## 7. CRUD de Edificio y de Usuario (7:45 - 9:15)
"Aquí listo, creo, edito y elimino edificios, y lo mismo con los usuarios, que solo gestiona el administrador. El id del usuario es su correo electrónico."

## 8. Reportes (9:15 - 10:15)
"Hay cuatro reportes con consultas de Spring Data JPA. Por ciudad y rango de pisos se resuelve con un método derivado del nombre. Por valor de administración, con filtros de ascensor y zona social, uso `@Query` con parámetros. Los de usuarios son por rol y por texto en el nombre o el dominio del correo."

## 9. Recuperación de clave (10:15 - 11:00)
"En 'Olvidaste tu clave' escribo mi correo. El sistema genera un token aleatorio, guarda solo su hash y envía por correo un enlace que vence en 30 minutos y se usa una sola vez; con ese enlace creo una clave nueva, que se guarda con BCrypt. La respuesta es la misma exista o no el correo."

## 10. Aplicación desplegada y commits (11:00 - 12:00)
"Esta es la aplicación publicada: [URL de la aplicación desplegada]. Y este es el repositorio, [URL del repositorio], con el historial de commits: creación del proyecto, configuración, entidades, repositorios, servicios, controladores, plantillas, autenticación, reportes, recuperación de clave y despliegue, todos hechos por mí a medida que avanzaba. Muchas gracias."
