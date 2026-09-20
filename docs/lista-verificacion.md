# Lista de verificación final (Spring MVC + Thymeleaf, Ejercicio 13)

No tuve el texto original de los 14 puntos de la actividad; esta lista sigue los requisitos del enunciado.

| # | Requisito | Estado |
|---|---|---|
| 1 | Spring Boot 3.x + Spring MVC + Thymeleaf + Spring Data JPA, Java 17, Maven | Cumplido (Boot 3.5.16) |
| 2 | Solo `@Controller`, sin `@RestController`/`@ResponseBody`/JSON (grep = 0 coincidencias) | Cumplido |
| 3 | Estructura por capas (model, repository, service, controller, config) | Cumplido (plan de respaldo) |
| 4 | Plantillas Thymeleaf con fragmentos (`th:fragment`) | Cumplido |
| 5 | Inyección de dependencias por constructor | Cumplido |
| 6 | Formularios con `th:object`/`th:field`, `@Valid`, `BindingResult`, PRG | Cumplido |
| 7 | Base de datos relacional con `schema.sql` y `data.sql` (3 usuarios, 10 edificios) | Cumplido |
| 8 | CRUD de Usuario y de Edificio con validaciones | Cumplido |
| 9 | Cuatro reportes parametrizados (JPA) | Cumplido |
| 10 | Login, sesión, logout, protección de páginas y control por rol | Cumplido |
| 11 | Recuperación de clave por correo con token de un solo uso y BCrypt | Cumplido (probado en local; falta la clave de Brevo para el envío real en producción) |
| 12 | Repositorio público con historial de commits propio | Cumplido |
| 13 | Word de evidencias, PDF de ficha, guion, guía de estudio | Cumplido (falta el enlace del video) |
| 14 | Aplicación desplegada y video de sustentación | Despliegue cumplido: https://edificios-spring-mvc.onrender.com (recuperación de clave real pendiente de SMTP); video: lo graba Miguel |
