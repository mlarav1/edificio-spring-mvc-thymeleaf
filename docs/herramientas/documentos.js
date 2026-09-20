// Genera docs/Evidencias-Edificio-Spring-MVC.docx y docs/Ficha-entrega-Spring-MVC.pdf
// Uso: node documentos.js [URL_APP_DESPLEGADA]
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const { Document, Packer, Paragraph, TextRun, ImageRun, HeadingLevel, AlignmentType, PageBreak } = require('docx');
const { chromium } = require('playwright');

const ROOT = path.join(__dirname, '..', '..');
const DOCS = path.join(ROOT, 'docs');
const REPO = 'https://github.com/mlarav1/edificio-spring-mvc-thymeleaf';
const APP = process.argv[2] || '';
const D = { nombre: 'Miguel Lara', codigo: '7502510046', semestre: 'Cuarto (4)', asignatura: 'Desarrollo Web',
  uni: 'Universidad de Cartagena · Ingeniería de Software',
  actividad: 'Spring Boot MVC con Thymeleaf: desarrollo web basado en framework (Unidad 2, individual)',
  ejercicio: '13 - Edificio',
  guia: 'Estructura estándar de Spring MVC por capas (plan de respaldo; no se pudo abrir la carpeta de guías del profesor)' };

const p = (t) => new Paragraph({ spacing: { after: 120 }, children: [new TextRun(t)] });
const h1 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_1, spacing: { before: 240, after: 120 }, children: [new TextRun(t)] });
const li = (t) => new Paragraph({ bullet: { level: 0 }, children: [new TextRun(t)] });
const code = (t) => new Paragraph({ spacing: { after: 60 }, children: [new TextRun({ text: t, font: 'Consolas', size: 18 })] });

function pngSize(f) { const b = fs.readFileSync(f); return { w: b.readUInt32BE(16), h: b.readUInt32BE(20) }; }
let fig = 0;
function figura(archivo, pie, maxW = 580, maxH = 620) {
  const f = path.join(DOCS, archivo); const { w, h } = pngSize(f);
  const e = Math.min(maxW / w, maxH / h); fig++;
  return [
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 120 }, children: [new ImageRun({ type: 'png', data: fs.readFileSync(f), transformation: { width: Math.round(w * e), height: Math.round(h * e) } })] }),
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 200 }, children: [new TextRun({ text: `Figura ${fig}. ${pie}`, italics: true, size: 20 })] }),
  ];
}

const commits = execSync('git log --reverse --format=%h%x09%ad%x09%s --date=format:%Y-%m-%d', { cwd: ROOT }).toString('utf8').trim().split(/\r?\n/);

const hijos = [
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 1800, after: 300 }, children: [new TextRun({ text: 'Universidad de Cartagena', bold: true, size: 36 })] }),
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 900 }, children: [new TextRun({ text: 'Ingeniería de Software', size: 28 })] }),
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 300 }, children: [new TextRun({ text: 'Evidencias de la actividad', bold: true, size: 40 })] }),
  new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 900 }, children: [new TextRun({ text: 'Spring Boot MVC + Thymeleaf · Ejercicio 13 - Edificio', size: 30 })] }),
  ...[['Estudiante', D.nombre], ['Código', D.codigo], ['Semestre', D.semestre], ['Asignatura', D.asignatura], ['Actividad', D.actividad], ['Guía utilizada', D.guia], ['Repositorio', REPO], ['Aplicación desplegada', APP || '(pendiente de publicar)']]
    .map(([k, v]) => new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 100 }, children: [new TextRun({ text: k + ': ', bold: true }), new TextRun(v)] })),
  new Paragraph({ children: [new PageBreak()] }),

  h1('Acceso a la aplicación y usuarios de prueba'),
  p('La aplicación desplegada está en la dirección indicada en la portada. Nota: el servicio gratuito se duerme tras un rato sin uso, por lo que la primera carga puede tardar cerca de 50 segundos.'),
  li('admin@edificios.com · clave Admin123 · rol ADMIN: gestiona edificios y usuarios, y ve todos los reportes.'),
  li('operador@edificios.com · clave Operador123 · rol OPERADOR: gestiona edificios.'),
  li('consulta@edificios.com · clave Consulta123 · rol CONSULTA: solo lectura.'),
  p('El correo es el identificador (id) del usuario. Para probar la recuperación de clave se usa la opción «¿Olvidaste tu clave?» del login con un correo registrado: el sistema envía un enlace con token (vigente 30 minutos y de un solo uso).'),
  new Paragraph({ children: [new PageBreak()] }),

  h1('1. Descripción del ejercicio'),
  p('El ejercicio 13 pide administrar edificios (nombre, metros cuadrados, altura, pisos, apartamentos, oficinas, parqueadero, piscinas, país, departamento, ciudad, ascensor, valor de administración y zona social; se agregó un id técnico). Además hay una entidad Usuario (id, clave, nombre, rol) con login, control de acceso por rol, recuperación de clave por correo y cuatro reportes parametrizados.'),
  p('La aplicación está hecha con Spring Boot 3.5.16, Spring MVC con renderizado del lado del servidor, Thymeleaf y Spring Data JPA sobre PostgreSQL. No es una API RESTful: los controladores son @Controller y retornan el nombre de una plantilla.'),

  h1('2. Estructura del proyecto y guía utilizada'),
  p('No fue posible abrir la carpeta de guías del profesor; se siguió la estructura estándar de Spring MVC por capas bajo el paquete co.edu.unicartagena.edificios:'),
  li('model: Usuario.java y Edificio.java (entidades JPA con Bean Validation).'),
  li('repository: UsuarioRepository.java y EdificioRepository.java (interfaces JpaRepository).'),
  li('service: EdificioService, UsuarioService, AuthService, CorreoService y NegocioException.'),
  li('controller: EdificioController, UsuarioController, AuthController, HomeController y GlobalModelAdvice.'),
  li('config: AuthInterceptor, WebConfig y PasswordConfig.'),
  li('templates: edificios/, usuarios/, auth/, index.html, error.html y fragments/layout.html.'),

  h1('3. Entity/Model'),
  p('Las entidades se mapean a las tablas con anotaciones JPA. Edificio incluye anotaciones de validación (@NotBlank, @DecimalMin, @Min) que se comprueban con @Valid.'),
  ...figura('capturas-codigo/01-entidad-usuario.png', 'Entidad Usuario (id = correo electrónico).', 520, 420),
  ...figura('capturas-codigo/02-entidad-edificio.png', 'Entidad Edificio con Bean Validation.', 560, 560),

  h1('4. Repository'),
  p('Los repositorios extienden JpaRepository; Spring Data genera su implementación. Los reportes usan métodos derivados del nombre y @Query con parámetros.'),
  ...figura('capturas-codigo/03-repositorio-usuario.png', 'UsuarioRepository: consulta derivada y @Query.', 560, 400),
  ...figura('capturas-codigo/08-reportes-repositorio.png', 'EdificioRepository: reportes por ciudad/pisos y por administración.', 560, 480),

  h1('5. Service e inyección de dependencias'),
  p('Los servicios reciben sus dependencias por constructor (por ejemplo EdificioService recibe EdificioRepository). Contienen la lógica de negocio, como la regla de que un edificio de más de 6 pisos necesita ascensor.'),
  ...figura('capturas-codigo/04-servicio-edificio.png', 'EdificioService con inyección por constructor.', 560, 500),

  h1('6. Controller, Model y Thymeleaf (binding de formularios)'),
  p('El controlador carga datos en el Model y retorna el nombre de la plantilla. El formulario usa th:object y th:field para el binding; @Valid y BindingResult validan; tras guardar se redirige (Post/Redirect/Get).'),
  ...figura('capturas-codigo/05-controlador-edificio.png', 'EdificioController (@Controller).', 560, 640),
  ...figura('capturas-codigo/06-plantilla-thymeleaf-formulario.png', 'Plantilla edificios/form.html con th:object y th:field.', 560, 520),

  h1('7. Flujo MVC completo'),
  p('Guardar un edificio: navegador → POST /edificios/guardar → AuthInterceptor → EdificioController.guardar (binding + @Valid) → EdificioService.guardar → EdificioRepository.save → PostgreSQL → redirect a /edificios → EdificioController.listar (Model) → edificios/lista.html → HTML.'),

  h1('8. Sesión y control de acceso'),
  p('AuthController guarda el usuario en HttpSession al autenticar (BCrypt) y cambia el id de sesión. AuthInterceptor se ejecuta antes de cada controlador: sin sesión redirige a /login; /usuarios/** solo ADMIN; crear, editar y eliminar edificios solo ADMIN u OPERADOR.'),
  ...figura('capturas-codigo/07-verificacion-sesion.png', 'AuthInterceptor: verificación de sesión y rol.', 560, 520),
  ...figura('capturas/01-login.png', 'Pantalla de inicio de sesión.', 460, 400),
  ...figura('capturas/03-inicio-menu.png', 'Inicio con el menú (rol ADMIN).', 560, 400),
  ...figura('capturas/14-acceso-denegado-consulta.png', 'Un usuario CONSULTA recibe acceso denegado en usuarios.', 560, 400),

  h1('9. CRUD de Edificio y de Usuario'),
  ...figura('capturas/04-edificios-listado.png', 'Listado de edificios.', 580, 420),
  ...figura('capturas/05-edificio-formulario.png', 'Formulario de edificio.', 580, 460),
  ...figura('capturas/12-validacion-error.png', 'Validación con @Valid: mensaje junto al campo, conservando lo digitado.', 580, 460),
  ...figura('capturas/08-usuarios-listado.png', 'Listado de usuarios (solo ADMIN).', 580, 360),
  ...figura('capturas/09-usuario-formulario.png', 'Formulario de usuario.', 580, 360),

  h1('10. Reportes'),
  li('Edificio 1: por ciudad y rango de pisos.'),
  li('Edificio 2: por valor de administración, con filtro de ascensor y zona social.'),
  li('Usuario 1: por rol.'),
  li('Usuario 2: por texto en el nombre o en el correo/dominio.'),
  ...figura('capturas/06-reporte-edificio-ciudad-pisos.png', 'Reporte de edificios por ciudad y pisos.', 580, 460),
  ...figura('capturas/07-reporte-edificio-administracion.png', 'Reporte de edificios por valor de administración.', 580, 460),
  ...figura('capturas/10-reporte-usuario-rol.png', 'Reporte de usuarios por rol.', 580, 400),
  ...figura('capturas/11-reporte-usuario-texto.png', 'Reporte de usuarios por texto.', 580, 400),

  h1('11. Recuperación de clave'),
  p('AuthService genera un token aleatorio de 256 bits, guarda solo su hash SHA-256 (tabla token_recuperacion, vigencia de 30 minutos) y CorreoService envía por correo el enlace /restablecer?token=... (API de Brevo o SMTP). El token sirve una sola vez; la nueva clave se guarda con BCrypt. La respuesta es la misma exista o no el correo.'),
  ...figura('capturas-codigo/09-recuperacion-clave.png', 'Recuperación de clave en AuthService.', 560, 560),
  ...figura('capturas/02-recuperar-clave.png', 'Formulario de recuperación de clave.', 460, 360),
  ...figura('capturas/13-recuperar-clave-enviada.png', 'Confirmación tras solicitar la recuperación.', 460, 360),

  h1('12. Historial de commits'),
  p(`El repositorio tiene ${commits.length} commits hechos a medida que se construía el proyecto.`),
  ...commits.map((c) => { const [h, f, s] = c.split('\t'); return code(`${h}  ${f}  ${s}`); }),
];

(async () => {
  const doc = new Document({ styles: { default: { document: { run: { font: 'Calibri', size: 22 } } } }, sections: [{ children: hijos }] });
  fs.writeFileSync(path.join(DOCS, 'Evidencias-Edificio-Spring-MVC.docx'), await Packer.toBuffer(doc));

  const a = (u) => u ? `<a href="${u}">${u}</a>` : '<span class="blanco">______________________________</span>';
  const html = `<html><head><meta charset="utf-8"><style>
body{font-family:'Segoe UI',Arial,sans-serif;margin:50px;color:#222}h1{color:#1f4e79;border-bottom:3px solid #1f4e79;padding-bottom:8px}
table{width:100%;border-collapse:collapse;margin-top:20px}td{padding:12px 10px;border-bottom:1px solid #ccc;vertical-align:top}td:first-child{width:30%;font-weight:700;color:#1f4e79}
a{color:#1f4e79}.blanco{color:#888}</style></head><body>
<h1>Ficha de entrega</h1><p>${D.uni}</p><table>
<tr><td>Estudiante</td><td>${D.nombre} · Código ${D.codigo}</td></tr>
<tr><td>Semestre</td><td>${D.semestre}</td></tr><tr><td>Asignatura</td><td>${D.asignatura}</td></tr>
<tr><td>Actividad</td><td>${D.actividad}</td></tr><tr><td>Ejercicio</td><td>${D.ejercicio}</td></tr>
<tr><td>Guía utilizada</td><td>${D.guia}</td></tr>
<tr><td>Repositorio</td><td>${a(REPO)}</td></tr>
<tr><td>Video de sustentación</td><td>${a('')}</td></tr>
<tr><td>Aplicación desplegada</td><td>${a(APP)}</td></tr></table></body></html>`;
  const b = await chromium.launch(); const pg = await b.newPage();
  await pg.setContent(html); await pg.pdf({ path: path.join(DOCS, 'Ficha-entrega-Spring-MVC.pdf'), format: 'A4', printBackground: true });
  await b.close(); console.log('ok');
})();
