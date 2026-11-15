# Marketplace de Videojuegos — Backend

Proyecto de la asignatura **Programación Avanzada**, Universidad del Quindío.

## Integrantes

- Juan David Arango Valencia
- Erwin Johannes Willem Harder Garzón

## Nicho

Marketplace de videojuegos digitales: los **vendedores** publican ofertas de juegos (con plataforma, precio y stock) y los **compradores** las adquieren, reciben una **clave digital** de activación, pueden dejar **comentarios** sobre lo comprado y solicitar **reembolsos**. El sistema maneja autenticación con roles (`VENDEDOR` / `COMPRADOR`) mediante JWT.

## Arquitectura

El backend sigue **Domain-Driven Design (DDD)** por capas:

- `domain`: entidades, value objects y reglas de negocio puras (sin dependencias de Spring/JPA).
- `application`: casos de uso que orquestan el dominio, DTOs de entrada/salida.
- `infrastructure`: adaptadores — controllers REST, persistencia JPA, seguridad (JWT), configuración.

Los agregados se referencian entre sí **únicamente por id (`String`)**, nunca por relaciones JPA (`@ManyToOne`), para mantener los límites de cada agregado bien definidos.

## Tecnologías

- Java 21, Spring Boot 3.4.1
- Spring Web, Spring Data JPA, Spring Security (JWT con `jjwt`)
- Base de datos H2 en memoria
- Gradle
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito

## Cómo ejecutar

Requiere JDK 21.

```bash
# Linux / macOS
./gradlew bootRun

# Windows
.\gradlew bootRun
```

La aplicación queda disponible en `http://localhost:8080`.

### Swagger / documentación interactiva

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Para probar endpoints protegidos: hacer `POST /api/auth/login`, copiar el `token` de la respuesta y pegarlo en el botón **Authorize** de Swagger (sin el prefijo `Bearer`, Swagger lo agrega solo).

### Consola H2 (opcional)

- `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:ecommercedb`, usuario `sa`, contraseña en blanco.

### Configuración del JWT

La clave de firma y la duración del token están en `application.properties` (`jwt.secret` y `jwt.expiration`, en milisegundos). La clave se puede sobrescribir con la variable de entorno `JWT_SECRET` sin tocar el código.

### Ejecutar las pruebas

```bash
.\gradlew test
```

Reporte HTML en `build/reports/tests/test/index.html`.

### Ejecutar con MariaDB (opcional)

Por defecto la aplicación usa H2 en memoria (los datos se pierden al reiniciar). Para persistir los datos hay un perfil `mariadb`:

```powershell
docker compose up -d
.\gradlew bootRun --args='--spring.profiles.active=mariadb'
```

- `docker-compose.yml` levanta MariaDB 11 con la base `ecommerce` (usuario y clave `ecommerce`).
- Los datos de conexión se pueden cambiar con las variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD`.
- El perfil usa `ddl-auto=update` (las tablas se crean una vez y los datos se conservan) y carga los datos de prueba con `data-mariadb.sql` (`INSERT IGNORE`, así reiniciar no duplica nada).
- Las pruebas automatizadas siguen usando H2.

## Usuarios de prueba

La base se inicializa (`data.sql`) con dos usuarios. La contraseña de ambos es **`clave123`** (son datos de prueba: para iniciar sesión sirve, pero una contraseña nueva debe cumplir la regla de 8 caracteres, mayúscula y número).

| Email | Rol | Password |
|---|---|---|
| `admin@ecommerce.com` | VENDEDOR | `clave123` |
| `juan@gmail.com` | COMPRADOR | `clave123` |

También hay 2 ofertas (`o-101` Zelda: Tears of the Kingdom, `o-102` God of War Ragnarok) y 3 claves digitales de prueba precargadas.

## Endpoints

### Autenticación (`/api/auth`) — públicos

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Autentica con email/contraseña y devuelve un token JWT |
| POST | `/api/auth/recuperar` | Pide recuperar la contraseña: envía un código de 6 dígitos al correo (vale 15 minutos). Responde siempre `202` con el mismo mensaje, exista o no el correo |
| POST | `/api/auth/restablecer` | Cambia la contraseña con `email`, `codigo` y `contrasenaNueva` (debe cumplir la política). El código es de un solo uso y se bloquea tras 5 intentos fallidos. Cualquier fallo responde `422 "El código no es válido o expiró"` |

### Usuarios (`/api/usuarios`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/usuarios` | Público | Registra un nuevo usuario (contraseña con BCrypt). La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número (si no, 422 con el motivo) |
| GET | `/api/usuarios/me` | Autenticado | Perfil del usuario del token |
| PUT | `/api/usuarios/me` | Autenticado | Edita nombre, teléfono, fecha de nacimiento y `fotoPerfil` (URL); el vendedor además `descripcion` y `datosPago` (un comprador que los envía recibe 422). El email y el rol no cambian |
| PUT | `/api/usuarios/me/contrasena` | Autenticado | Cambia la contraseña: exige la actual (`contrasenaActual`, `contrasenaNueva`); responde 204 |
| DELETE | `/api/usuarios/me` | Autenticado | Borrado lógico de la cuenta: ya no puede iniciar sesión ni usar su token. No se permite con ofertas publicadas (vendedor) ni con compras pendientes (comprador) |

### Ofertas (`/api/ofertas`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/ofertas` | Público | Lista/busca ofertas (filtros: `busqueda`, `plataforma`, `precioMinimo`, `precioMaximo`, `calificacionMinima` de 1 a 5 según el promedio de comentarios, `orden` = `NOMBRE` (por defecto), `PRECIO_ASC`, `PRECIO_DESC`, `POPULARIDAD` o `CALIFICACION`; paginación `page`/`size`). Cada oferta trae `imagenPrincipal` (la primera de la galería), `promedioCalificacion` y `totalComentarios` |
| GET | `/api/ofertas/mas-vendidas` | Público | Top ofertas más vendidas (parámetro `limite`) |
| GET | `/api/ofertas/{id}` | Público | Detalle de una oferta publicada, con su promedio de calificación (una oferta eliminada o sin publicar responde 404) |
| POST | `/api/ofertas` | VENDEDOR | Publica una nueva oferta. El vendedor se toma del token (no se envía `vendedorId`) |
| PUT | `/api/ofertas/{id}` | VENDEDOR | Edita una oferta propia (título, precio, stock, plataforma) y opcionalmente su galería `imagenes` (hasta 10 URLs http/https; si no se envía, la galería no cambia; si se envía, la reemplaza). Una oferta publicada no puede quedarse sin stock ni editarse si está eliminada |
| DELETE | `/api/ofertas/{id}` | VENDEDOR | Elimina (lógicamente) una oferta propia, solo si no tiene compras de las últimas 48 horas sujetas a reembolso |
| POST | `/api/ofertas/{id}/claves` | VENDEDOR | El dueño de la oferta le carga una clave digital (`codigo`, `maxActivaciones`, `transferible`) |

### Vendedor (`/api/vendedor`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/vendedor/ofertas` | VENDEDOR | Lista las ofertas del vendedor del token (sin las eliminadas) |

### Favoritos (`/api/favoritos`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/favoritos` | COMPRADOR | Ofertas favoritas del comprador del token, la guardada más recientemente primero (las ofertas eliminadas dejan de aparecer) |
| POST | `/api/favoritos/{ofertaId}` | COMPRADOR | Guarda una oferta publicada como favorita (404 si no existe o no está publicada; 422 si ya estaba) |
| DELETE | `/api/favoritos/{ofertaId}` | COMPRADOR | Quita una oferta de los favoritos (404 si no estaba) |

### Compras (`/api/compras`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/compras` | COMPRADOR | Registra una compra sobre una oferta. El comprador se toma del token, no del cuerpo (solo se envía `ofertaId`) |
| GET | `/api/compras/{id}` | Autenticado | Detalle de una compra propia |
| PUT | `/api/compras/{id}/confirmar` | COMPRADOR | Confirma una compra propia (pasa a COMPLETADA) |
| POST | `/api/compras/{id}/clave` | COMPRADOR | Entrega la clave digital de una compra completada; la clave queda canjeada |
| POST o PUT | `/api/compras/{id}/reembolso` | COMPRADOR | Solicita reembolso de una compra propia, dentro de 48 horas y con la clave sin canjear (si la compra es de otro comprador responde 404) |
| GET | `/api/compras?desde=&hasta=&estado=` | Autenticado | Historial de compras del usuario del token, el más reciente primero. Filtros opcionales: fechas `yyyy-MM-dd` (inclusivo) y estado |

### Comentarios (`/api/comentarios`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/comentarios` | COMPRADOR | Deja un comentario con calificación de 1 a 5 (máx. 500 caracteres) sobre una oferta con compra completada. El autor sale del token; un comentario por compra |
| GET | `/api/comentarios?ofertaId=` | Público | Devuelve `{promedioCalificacion, total, comentarios}` con el más reciente primero |
| PUT | `/api/comentarios/{id}/respuesta` | VENDEDOR | El vendedor dueño de la oferta responde el comentario (una sola respuesta, máx. 500 caracteres) |

### Panel del vendedor (comentarios y métricas)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/vendedor/ventas?desde=&hasta=&estado=` | VENDEDOR | Historial de compras sobre las ofertas del vendedor (incluidas las eliminadas), el más reciente primero. Filtros opcionales: rango de fechas `yyyy-MM-dd` (inclusivo) y `estado` (`PENDIENTE`, `COMPLETADA`, `REEMBOLSADA`) |
| GET | `/api/vendedor/comentarios?sinResponder=true` | VENDEDOR | Comentarios de las ofertas del vendedor; el filtro deja solo los pendientes de respuesta |
| GET | `/api/vendedor/metricas?desde=&hasta=` | VENDEDOR | Por oferta: ventas completadas, calificación promedio y total de comentarios. Fechas opcionales (`yyyy-MM-dd`, inclusivas) |

### Notificaciones (`/api/notificaciones`) — cualquier usuario autenticado

Cada usuario solo ve las suyas (la de otro usuario responde 404).

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/notificaciones?soloNoLeidas=` | Lista las notificaciones del usuario del token, la más reciente primero |
| GET | `/api/notificaciones/no-leidas/cantidad` | Cantidad de no leídas (para el globo del encabezado) |
| PUT | `/api/notificaciones/{id}/leida` | Marca una como leída (204) |
| PUT | `/api/notificaciones/leidas` | Marca todas como leídas; devuelve `{ "marcadas": n }` |

Se generan solas: al **vendedor** le avisan una venta nueva (`VENTA_REALIZADA`), una solicitud de reembolso (`REEMBOLSO_SOLICITADO`, también por correo) y un comentario nuevo (`COMENTARIO_NUEVO`, también por correo); al **comprador** le avisan la confirmación de su compra (`COMPRA_CONFIRMADA`, también por correo). Un fallo del correo nunca deshace la operación.

### Subida de imágenes (`/api/archivos`) — autenticado

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/archivos/firma?carpeta=ofertas\|perfiles` | Devuelve la firma para subir una imagen directo a Cloudinary desde el cliente (el secreto de la API nunca sale del servidor). El cliente guarda después la URL resultante en la oferta o el perfil. Responde 503 si Cloudinary no está configurado |

### Correo y Cloudinary (variables de entorno)

| Variable | Para qué sirve | Por defecto |
|---|---|---|
| `CORREO_HABILITADO` | `true` envía los correos por SMTP; `false` solo los escribe en el log | `false` |
| `SMTP_HOST`, `SMTP_PORT`, `SMTP_USUARIO`, `SMTP_CLAVE` | Servidor SMTP (por ejemplo Gmail con una contraseña de aplicación) | `smtp.gmail.com`, `587` |
| `CORREO_REMITENTE` | Dirección que aparece como remitente | `no-reply@ecommerce.local` |
| `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` | Credenciales de Cloudinary para la firma de subida | vacías |

### Reglas de negocio de la compra

- Un comprador no puede comprar dos veces la misma oferta mientras la compra siga activa (si la reembolsa, puede volver a comprarla).
- Solo se venden ofertas publicadas y con una clave digital disponible; el vendedor carga las claves con `POST /api/ofertas/{id}/claves`.
- El reembolso solo procede dentro de 48 horas y mientras la clave no se haya canjeado (recibir la clave con `POST /api/compras/{id}/clave` la canjea). Al reembolsar, la clave queda invalidada.

### Códigos de error

| Código | Cuándo |
|---|---|
| 400 | Datos de entrada inválidos (`@Valid`): el mensaje indica qué campo falló |
| 401 | Sin token o token inválido |
| 403 | Token válido pero con el rol equivocado |
| 404 | El recurso no existe (oferta, compra, clave...), con el mismo formato en todos los casos |
| 422 | La petición es válida pero rompe una regla de negocio (`ReglaDominioException`) |
| 503 | Un servicio externo no está configurado (por ejemplo, la firma de Cloudinary sin credenciales) |

> Nota: las rutas marcadas "Autenticado" exigen un JWT válido (cualquier rol); las de VENDEDOR/COMPRADOR exigen además ese rol específico.

## Tabla de correcciones (Entrega 1 → Entrega 2)

Durante la integración de la rama de seguridad (`feature/erwin`) con la rama principal de dominio (`feature/juan`) se detectaron y corrigieron los siguientes defectos:

| # | Problema detectado | Causa | Corrección |
|---|---|---|---|
| 1 | Consultas JPQL de `OfertaJpaDataRepository` y `ClaveDigitalJpaDataRepository` fallaban al arrancar | Usaban rutas de relación JPA inexistentes (`c.oferta.id`) en vez del campo plano `ofertaId`, violando el patrón DDD del proyecto (agregados solo se referencian por id) | Se reescribieron las consultas usando los campos planos (`c.ofertaId`) |
| 2 | `ComentarioJpaDataRepository` no compilaba | Método `obtenerCalificacionPromedioPorOferta` referenciaba un campo `calificacion` que no existe en la entidad | Se eliminó el método inválido |
| 3 | La aplicación no arrancaba (`data.sql` fallaba) | Hash BCrypt de los usuarios semilla tenía 57 caracteres en vez de 60 (inválido); faltaban columnas `NOT NULL` (`max_activaciones`, `transferible`) en los inserts de `claves_digitales` | Se generó un hash BCrypt válido de 60 caracteres y se completaron las columnas faltantes |
| 4 | El registro de usuario guardaba la contraseña en texto plano | `RegistrarUsuario` no inyectaba `PasswordEncoder` | Se inyectó `PasswordEncoder` (BCrypt) y se cifra la contraseña antes de persistir; se añadió validación de email duplicado |
| 5 | Existían dos endpoints de registro de usuario (`/api/usuarios` y `/api/auth/registro`) | Duplicación al mezclar las dos ramas | Se eliminó `/api/auth/registro`; el registro vive solo en `UsuarioController` (`POST /api/usuarios`) |
| 6 | Swagger UI no cargaba (`Failed to load remote configuration`) | La ruta personalizada `/api-docs` no estaba permitida en `SecurityConfig` | Se agregó `/api-docs`, `/api-docs/**` a las rutas públicas |
| 7 | Swagger fallaba con `NoSuchMethodError: ControllerAdviceBean.<init>` | `springdoc-openapi-starter-webmvc-ui` 2.6.0 es incompatible con Spring Framework 6.2.x (el que trae Boot 3.4.1) | Se actualizó la dependencia a la versión 2.7.0 |
| 8 | El botón "Authorize" no existía en Swagger | No había ningún `@SecurityScheme` definido | Se creó `OpenApiConfig` con `@SecurityScheme` de tipo bearer/JWT |
| 9 | El token autorizado en Swagger no se enviaba en las peticiones | `@SecurityScheme` solo define el mecanismo, no lo activa globalmente | Se agregó `@OpenAPIDefinition(security = @SecurityRequirement(name = "bearerAuth"))` |
| 10 | Peticiones sin permisos devolvían 401 en vez de 403 (rol incorrecto) | `response.sendError(...)` en los manejadores de excepciones de seguridad disparaba un *forward* interno a `/error`, que Spring Security volvía a evaluar y sobrescribía el código de estado original | Se reemplazó `sendError(...)` por escritura directa de la respuesta (`setStatus` + `getWriter().write(...)`) |

### Correcciones arquitectónicas (casos de uso)

Durante la revisión del proyecto se identificaron problemas
arquitectónicos relacionados con la separación de responsabilidades
entre la capa de aplicación y el dominio. Se realizaron las siguientes
correcciones principales:

| # | Problema detectado | Causa | Corrección |
|---|---|---|---|
| 1 | Los casos de uso contenían lógica de negocio y no se limitaban a orquestar las operaciones. | Se mezclaban las responsabilidades de la capa de aplicación con las reglas que debían pertenecer al dominio. | Se refactorizaron los casos de uso para delegar las reglas de negocio a las entidades y servicios de dominio, manteniendo en aplicación únicamente la orquestación de las operaciones. |

## Correcciones tras la retroalimentación de la Entrega 2

| # | Observación | Qué cambiamos | Dónde se ve |
|---|---|---|---|
| 1 | El id del comprador se tomaba del cuerpo de la petición, así que un COMPRADOR podía comprar, reembolsar o comentar a nombre de otro | El id sale del usuario autenticado (`@AuthenticationPrincipal UsuarioPrincipal`); se quitó `compradorId` de los DTO de compra y comentario; el historial usa el usuario del token; el reembolso solo funciona sobre compras propias (`Compra.perteneceA`) | `CompraController`, `ComentarioController`, `ConsultaComprasController`, `UsuarioPrincipal`, `IdentidadDelTokenIntegracionTest` |
| 2 | La regla de negocio rota devolvía 400 | `ReglaDominioException` ahora responde 422; los datos de entrada inválidos siguen en 400 con mensaje por campo | `GlobalExceptionHandler`, `CompraControllerErroresTest` |
| 3 | "No encontrado" se manejaba distinto en ofertas y compras (400 con mensaje en un caso, 404 genérico en el otro) | Nueva `RecursoNoEncontradoException`: todos los casos de uso responden 404 con un mensaje claro | `RecursoNoEncontradoException`, casos de uso, `GlobalExceptionHandler` |
| 4 | Pruebas de casos de uso cortas frente a los diez casos de uso | Pruebas nuevas para los diez casos de uso, incluidos los que fallan por regla de negocio | `src/test/.../application/usecase` |
| 5 | La clave y la duración del JWT estaban escritas en el código | Pasaron a `application.properties` (la clave puede sobrescribirse con `JWT_SECRET`) | `application.properties`, `JwtUtils` |
| 6 | El vendedor se tomaba del cuerpo al publicar y cualquier vendedor podía eliminar ofertas ajenas | El vendedor sale del token; solo el dueño elimina (o carga claves) y no se puede eliminar con compras recientes sujetas a reembolso | `OfertaController`, `EliminarOferta`, `FlujoDeClavesIntegracionTest` |
| 7 | `PUT /confirmar` lo podía usar cualquier usuario | Solo el comprador dueño de la compra puede confirmarla | `ConfirmarCompraUseCase` |
| 8 | El comprador nunca recibía la clave y el vendedor no podía cargarlas | Endpoints `POST /api/compras/{id}/clave` y `POST /api/ofertas/{id}/claves`; el reembolso respeta el plazo de 48 horas y la clave canjeada | `ClaveDigitalController`, `EntregarClaveDigital`, `CargarClaveDigital`, `SolicitarReembolso` |
| 9 | Se podía comprar dos veces lo mismo y comprar ofertas eliminadas | `RegistrarCompra` rechaza ambos casos con 422 | `RegistrarCompra`, `RegistrarCompraReglasTest` |
| 10 | Los comentarios no tenían calificación, no se podía responderlos ni medir el desempeño de una oferta | Calificación 1–5, un comentario por compra, respuesta única del vendedor, bandeja de pendientes y métricas por rango de fechas | `Comentario`, `DejarComentario`, `ResponderComentario`, `ConsultarMetricasDelVendedor`, `ComentariosIntegracionTest` |
| 11 | El vendedor no podía corregir una oferta, no tenía galería de imágenes ni historial de ventas | `PUT /api/ofertas/{id}` (solo el dueño; las ajenas responden 404), galería de hasta 10 URLs validadas y `GET /api/vendedor/ventas` con filtros por fecha y estado | `Oferta.editar`, `Oferta.definirImagenes`, `EditarOferta`, `ConsultarVentasDelVendedor`, `GestionDeOfertasIntegracionTest` |
| 12 | Contraseñas débiles, sin gestión de la cuenta y catálogo sin filtros de precio ni calificación | `PoliticaContrasena` (8+, mayúscula y número), perfil (`/api/usuarios/me`), cambio de contraseña y borrado lógico (`Usuario.activo`); filtros de precio y calificación mínima en `GET /api/ofertas` | `PoliticaContrasena`, `Usuario`, `EditarPerfil`, `CambiarContrasena`, `EliminarCuenta`, `FiltroBusqueda`, `CuentaDeUsuarioIntegracionTest` |
| 13 | No había forma de guardar ofertas para después y los datos se perdían al reiniciar (solo H2) | Favoritos del comprador (con restricción única por comprador y oferta) y perfil `mariadb` con `docker-compose.yml` | `Favorito`, `AgregarFavorito`, `ListarFavoritos`, `FavoritoController`, `application-mariadb.properties`, `FavoritosIntegracionTest` |
| 14 | Revisión contra el documento del proyecto final: las tarjetas no traían el promedio de calificación, el detalle mostraba ofertas eliminadas, el historial del comprador no tenía filtros, el perfil no tenía foto ni datos del vendedor y no se podía ordenar por popularidad | `OfertaResponse` con `imagenPrincipal` y promedio; el detalle solo muestra ofertas publicadas; filtros del historial; foto, descripción y datos de pago; parámetro `orden`; reembolso también por `PUT` (como en el mapeo de la profesora) | `ConsultarCalificaciones`, `BuscarOfertas`, `ConsultarHistorialDeCompras`, `Usuario.definirPerfilExtendido`, `OrdenOfertas`, `CatalogoYPerfilIntegracionTest` |
| 15 | Faltaban las notificaciones, los correos, la recuperación de contraseña y la subida de imágenes que pide el documento del proyecto final | Notificaciones dentro de la app y por correo mediante el puerto `Notificador`; recuperación por código de 6 dígitos (15 min, un solo uso, 5 intentos); firma de subida a Cloudinary | `Notificador`, `NotificadorPersistente`, `NotificacionController`, `SolicitarRecuperacionContrasena`, `RestablecerContrasena`, `GenerarFirmaDeSubida`, `NotificacionesIntegracionTest`, `RecuperacionDeContrasenaIntegracionTest` |

## Pruebas automatizadas

El proyecto cuenta con pruebas unitarias (dominio y casos de uso), de integración con `@DataJpaTest` (repositorios) y de capa web con `@WebMvcTest` (controllers). Total actual: **223 pruebas**, todas en verde (incluye pruebas de los diez casos de uso, con casos que fallan por reglas de negocio).

## Pruebas avanzadas

### 1. Endpoint con seguridad (401, 403 y 200)
Probamos la ruta `DELETE /api/ofertas/{id}`, que solo puede usar un usuario con rol VENDEDOR.
Sin token responde 401, porque el sistema no sabe quién es.
Con token de un COMPRADOR responde 403: sabe quién es, pero no tiene permiso.
Con token de un VENDEDOR responde 200 y la oferta se marca como ELIMINADA (no se borra de la base).
Usamos `@SpringBootTest` y no `@WebMvcTest`, porque `@WebMvcTest` no carga nuestro `SecurityConfig`.
La guía usa `jwt()`, pero eso necesita el módulo `oauth2-resource-server` y nosotros tenemos nuestro
propio filtro JWT, así que generamos los tokens con nuestra clase `JwtUtils`.
Lo que más nos costó: en `OfertaControllerTest` salía 401 en una ruta pública, porque `@WebMvcTest`
aplicaba la seguridad por defecto de Spring y no la nuestra.

### 2. @DataJpaTest contra @SpringBootTest
`@DataJpaTest` levanta solo la parte de base de datos (entidades y repositorios) con una H2 en memoria,
por eso es más rápido. `@SpringBootTest` levanta toda la aplicación.
Como `@DataJpaTest` no carga nuestras clases propias, trajimos el adaptador y el mapper con `@Import`.
`@DataJpaTest` ya viene con `@Transactional`, que deshace lo guardado al terminar cada prueba;
en `@SpringBootTest` hay que ponerlo a mano si queremos lo mismo.
Sobre Spring Boot 4: usamos Spring Boot 3.4.1, así que `@DataJpaTest` sigue en su paquete de siempre
y no tuvimos que agregar ningún starter aparte.
Lo que más nos costó fue justo eso: la guía hablaba del starter de Boot 4 y tuvimos que revisar
nuestra versión para darnos cuenta de que no hacía falta.

### 3. Prueba de integración
Una prueba unitaria revisa una sola clase y lo demás lo simula con Mockito.
Nuestra prueba de integración hace una compra real: la petición pasa por la seguridad, el controller,
el caso de uso, el repositorio y la base de datos, sin simular nada.
Al final, con `compraRepository` y `ofertaRepository`, comprobamos que el stock bajó y que la compra quedó guardada.
También hay una segunda prueba: comprar una oferta que no existe responde 400 y no se guarda ninguna compra.
Conviene para los flujos más importantes; como es más lenta, los casos raros los probamos con unitarias.
Lo que más nos costó: los datos de prueba salen del `data.sql`, así que había que fijarse en los ids
y el stock que hay ahí para saber qué resultado esperar.