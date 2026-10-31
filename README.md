# Marketplace de Videojuegos — Backend

Proyecto de la asignatura **Programación Avanzada**, Universidad del Quindío.

## Integrantes

- Juan David Arango Valencia
- Erwin Harder Garzón

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

### Ejecutar las pruebas

```bash
.\gradlew test
```

Reporte HTML en `build/reports/tests/test/index.html`.

## Usuarios de prueba

La base se inicializa (`data.sql`) con dos usuarios. La contraseña de ambos es **`clave123`**.

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

### Usuarios (`/api/usuarios`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/usuarios` | Público | Registra un nuevo usuario (contraseña se cifra con BCrypt) |

### Ofertas (`/api/ofertas`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| GET | `/api/ofertas` | Público | Lista/busca ofertas (filtros: `busqueda`, `plataforma`, paginación `page`/`size`) |
| GET | `/api/ofertas/mas-vendidas` | Público | Top ofertas más vendidas (parámetro `limite`) |
| GET | `/api/ofertas/{id}` | Público | Consulta el detalle de una oferta |
| POST | `/api/ofertas` | VENDEDOR | Publica una nueva oferta |
| DELETE | `/api/ofertas/{id}` | VENDEDOR | Elimina una oferta |

### Compras (`/api/compras`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/compras` | COMPRADOR | Registra una compra sobre una oferta |
| PUT | `/api/compras/{id}/confirmar` | Autenticado | Confirma una compra (entrega la clave digital) |
| POST | `/api/compras/{id}/reembolso` | COMPRADOR | Solicita reembolso de una compra |
| GET | `/api/compras?compradorId=` | Autenticado | Historial de compras de un comprador |

### Comentarios (`/api/comentarios`)

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/comentarios` | Autenticado | Deja un comentario sobre una oferta comprada |
| GET | `/api/comentarios?ofertaId=` | Autenticado | Lista comentarios de una oferta |

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

## Pruebas automatizadas

El proyecto cuenta con pruebas unitarias (dominio y casos de uso), de integración con `@DataJpaTest` (repositorios) y de capa web con `@WebMvcTest` (controllers). Total actual: **18 pruebas**, todas en verde.

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