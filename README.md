# API REST Apartamentos

API REST para una plataforma de **renta de pisos, apartamentos y habitaciones** (estilo Airbnb):
clientes y propietarios, propiedades con imágenes, unidades rentables, disponibilidad, reservas,
pagos, reseñas y notificaciones. Incluye además una **interfaz gráfica web** para administrar los datos.
Sigue las convenciones REST: recursos en plural, métodos HTTP y códigos de estado adecuados.

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Spring Boot 4 | Framework (Spring Web MVC, Spring Data JPA, Bean Validation, Thymeleaf) |
| Hibernate / JPA | Mapeo objeto-relacional; crea las tablas automáticamente |
| MySQL 8 | Base de datos |
| Maven (Maven Wrapper) | Compilación y dependencias; no requiere instalar Maven |
| JUnit 5 + Mockito + H2 | Pruebas automáticas |
| Postman | Pruebas manuales de la API |

## Modelo de datos

```
Cliente (propietario) 1 ── * Propiedad 1 ── * UnidadRentable 1 ── * Reserva * ── 1 Cliente (huésped)
                              Propiedad 1 ── * PropiedadImagen           Reserva 1 ── * Pago
                         UnidadRentable 1 ── * Disponibilidad            Reserva 1 ── * Reseña
                                     Cliente 1 ── * Notificacion (remitente / destinatario)
```

| Entidad | Campos principales |
|---|---|
| Cliente | id, tipo (PROPIETARIO, CLIENTE, EMPRESA), nombres, apellidos, email (único), telefono, ine, estatus, password (cifrado, nunca se devuelve) |
| Propiedad | idPropiedad, propietario, tipo, titulo, descripcion, direccion, ciudad, precioNoche, capacidad, habitaciones, baños, estado |
| UnidadRentable | idUnidad, propiedad, nombre, tipo, capacidad, precioNoche, estado |
| Disponibilidad | idDisponibilidad, unidad, fechaInicio, fechaFin, disponible, precioEspecial |
| Reserva | idReservacion, unidad, cliente, fechaEntrada, fechaSalida, importeTotal, estado |
| Pago | idPago, reserva, cliente, importe, fechaVencimiento, fechaPago, referenciaPago, metodo, estado |
| Reseña | idResena, reserva, cliente, calificacionGeneral, calificacionComunicacion, comentario |
| Notificacion | idNotificacion, remitente, destinatario, asunto, mensaje, estado |

## Instalación y ejecución

### Requisitos previos

| Herramienta | Versión | Instalación en Windows |
|---|---|---|
| Java JDK | 21 | `winget install EclipseAdoptium.Temurin.21.JDK` |
| MySQL Server | 8.x | `winget install Oracle.MySQL` y configurarlo con **MySQL Configurator** |
| Git | — | `winget install Git.Git` |
| Postman | — | https://www.postman.com/downloads/ |

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/lopezalvarezjaqueline00-hub/api-apartamentos.git
   cd api-apartamentos
   ```
2. **Encender MySQL** (el servicio `MySQL84` o equivalente debe estar en *Running*).
   La base de datos `apartamentos` y sus tablas **se crean automáticamente** al arrancar.
3. **Configurar las variables de entorno** si tu usuario o contraseña de MySQL son distintos a los
   valores por defecto (ver la sección siguiente).
4. **Ejecutar la aplicación**
   ```bash
   .\mvnw.cmd spring-boot:run     # Windows (PowerShell)
   ./mvnw spring-boot:run         # Mac / Linux
   ```
   La primera ejecución descarga las dependencias. La API está lista cuando aparece
   `Started ...Application` y `Tomcat started on port 8000`.
5. **Comprobar**: abrir `http://localhost:8000/` (interfaz gráfica) y `http://localhost:8000/api/clientes` (debe responder `[]`).
6. **Detener**: `Ctrl + C` en la terminal.

Pruebas automáticas (no necesitan MySQL, usan H2 en memoria): `.\mvnw.cmd test`

## Variables de entorno

Las credenciales **no están escritas en el código**: `application.properties` las lee de variables
de entorno. Si una variable no existe se usa el valor por defecto, pensado para desarrollo local.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/apartamentos?createDatabaseIfNotExist=true` |
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | `root1234` |
| `SERVER_PORT` | Puerto de la API | `8000` |

Ejemplo para definirlas antes de ejecutar (PowerShell, solo para esa terminal):

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "mi_contraseña"
.\mvnw.cmd spring-boot:run
```

## Endpoints

Cada recurso tiene `GET` (todos), `GET /{id}`, `POST`, `PUT /{id}` y `DELETE /{id}`, más estas consultas:

| Recurso | Ruta base | Consultas y acciones extra |
|---|---|---|
| Clientes | `/api/clientes` | — |
| Propiedades | `/api/propiedades` | `GET /tipo/{tipo}`, `GET /propietario/{idPropietario}` |
| Imágenes | `/api/propiedad-imagenes` | `GET /propiedad/{idPropiedad}` |
| Unidades rentables | `/api/unidades` | `GET /propiedad/{idPropiedad}` |
| Disponibilidad | `/api/disponibilidades` | `GET /unidad/{idUnidad}` |
| Reservas | `/api/reservas` | — |
| Pagos | `/api/pagos` | `GET /reserva/{idReserva}`, `POST /{id}/confirmar`, `POST /{id}/reembolsar` |
| Reseñas | `/api/resenas` | `GET /reserva/{idReserva}` |
| Notificaciones | `/api/notificaciones` | `GET /destinatario/{idCliente}` |

Los cuerpos JSON de ejemplo de cada entidad están en `docs/json/`.

## Ejemplos de peticiones y respuestas

### Crear un cliente

**Petición**

```http
POST /api/clientes
Content-Type: application/json

{
  "tipo": "CLIENTE",
  "nombres": "Carlos",
  "apellidos": "Ramírez Soto",
  "email": "carlos.ramirez@correo.com",
  "telefono": "5512345678",
  "estatus": "ACTIVO",
  "password": "Huesped123"
}
```

**Respuesta** `201 Created`

```json
{
  "id": 2,
  "tipo": "CLIENTE",
  "nombres": "Carlos",
  "apellidos": "Ramírez Soto",
  "email": "carlos.ramirez@correo.com",
  "telefono": "5512345678",
  "fechaNacimiento": null,
  "ine": null,
  "direccion": null,
  "fechaRegistro": "2026-10-13T17:10:02.381",
  "estatus": "ACTIVO"
}
```

### Crear una reserva (el importe se calcula: 3 noches × 1200)

**Petición**

```http
POST /api/reservas
Content-Type: application/json

{
  "unidad": { "idUnidad": 1 },
  "cliente": { "id": 2 },
  "fechaEntrada": "2026-11-10T15:00:00",
  "fechaSalida": "2026-11-13T12:00:00",
  "estado": "CONFIRMADA"
}
```

**Respuesta** `201 Created`

```json
{
  "idReservacion": 1,
  "unidad": { "idUnidad": 1, "nombre": "Departamento completo", "tipo": "Departamento", "capacidad": 4, "precioNoche": 1200.00, "estado": "DISPONIBLE" },
  "cliente": { "id": 2, "nombres": "Carlos", "apellidos": "Ramírez Soto", "...": "..." },
  "fechaEntrada": "2026-11-10T15:00:00",
  "fechaSalida": "2026-11-13T12:00:00",
  "fechaCheckin": null,
  "fechaCheckout": null,
  "importeTotal": 3600.00,
  "estado": "CONFIRMADA",
  "comentario": null
}
```

### Consultar propiedades por tipo

**Petición**

```http
GET /api/propiedades/tipo/departamento
```

**Respuesta** `200 OK`

```json
[
  {
    "idPropiedad": 1,
    "propietario": { "id": 1, "nombres": "Laura", "apellidos": "Méndez Ruiz", "...": "..." },
    "tipo": "Departamento",
    "titulo": "Departamento céntrico con terraza",
    "ciudad": "Puebla",
    "precioNoche": 1200.00,
    "capacidad": 4,
    "estado": "DISPONIBLE",
    "...": "..."
  }
]
```

### Error de validación: fechas que se cruzan con otra reserva

**Petición**

```http
POST /api/reservas
Content-Type: application/json

{
  "unidad": { "idUnidad": 1 },
  "cliente": { "id": 2 },
  "fechaEntrada": "2026-11-12T15:00:00",
  "fechaSalida": "2026-11-14T12:00:00"
}
```

**Respuesta** `400 Bad Request`

```json
{
  "timestamp": "2026-10-13T17:12:40.912",
  "status": 400,
  "error": "La unidad ya está reservada en ese rango"
}
```

### Registro inexistente

**Petición**

```http
GET /api/clientes/999
```

**Respuesta** `404 Not Found`

_(sin cuerpo)_

## Interfaz gráfica

Con la aplicación encendida, abre **http://localhost:8000/** en el navegador.

| Pantalla | Ruta | Qué permite |
|---|---|---|
| Panel principal | `/` | Resumen con totales y registros recientes |
| Clientes | `/clientes` | Listar, crear, editar y eliminar clientes |
| Propiedades | `/propiedades` | Listar, crear, editar y eliminar propiedades |
| Unidades | `/unidades` | Unidades rentables de cada propiedad |
| Reservas | `/reservas` | Crear, editar y cancelar reservas |
| Pagos | `/pagos` | Registrar, confirmar y reembolsar pagos |

La interfaz usa los mismos servicios que la API, así que aplica las mismas reglas de negocio.

## Reglas de negocio

- El email del cliente es único y su contraseña se guarda cifrada con BCrypt; nunca aparece en las respuestas.
- Una propiedad debe tener un propietario existente; no se agregan unidades a una propiedad en mantenimiento.
- Una unidad no puede tener dos reservas activas que se crucen en fechas.
- `importeTotal` de la reserva se calcula: precio por noche de la unidad × número de noches.
- Los pagos generan su `referenciaPago` automáticamente, no pueden superar el importe de la reserva y siguen
  un flujo de estados (PENDIENTE → EN_PROCESO → PAGADO → REEMBOLSADO, o RECHAZADO / VENCIDO).
- Los pagos confirmados no se pueden eliminar.

## Códigos de respuesta y errores

| Código | Cuándo |
|---|---|
| `200 OK` | Consulta, actualización o acción correcta |
| `201 Created` | Registro creado |
| `204 No Content` | Registro eliminado |
| `400 Bad Request` | Datos inválidos o regla de negocio no cumplida |
| `404 Not Found` | El registro solicitado no existe |
| `409 Conflict` | Restricción de la base de datos (dato duplicado o registro relacionado) |

Los errores 400 y 409 devuelven un JSON con el motivo:

```json
{ "timestamp": "2026-10-13T16:37:50.935", "status": 400, "error": "Mensaje que explica el error" }
```

## Pruebas con Postman

1. Abrir Postman → **Import** → seleccionar `docs/API_Apartamentos.postman_collection.json`.
2. La colección define la variable `baseUrl` = `http://localhost:8000`.
3. Ejecutar las peticiones **en orden**, carpeta por carpeta, con **Send**.
4. Las peticiones cuyo nombre dice *"debe responder 400 / 404 / 409"* prueban el manejo de errores.

Alternativa: extensión **REST Client** de VS Code con `docs/pruebas-api.http`.

## Estructura del proyecto

```
src/main/java/com/api/apartamentos/
├── controllers/   → endpoints REST: reciben la petición y devuelven ResponseEntity
├── services/      → reglas de negocio y validaciones
├── repositories/  → acceso a datos con Spring Data JPA
└── models/        → entidades JPA (tablas)
src/main/resources/templates/   → pantallas de la interfaz gráfica (Thymeleaf)
src/main/resources/static/css/  → estilos de la interfaz
src/main/resources/application.properties   → puerto y conexión a MySQL (variables de entorno)
src/test/                                    → pruebas unitarias y de arranque
docs/                                        → colección de Postman, archivo .http y JSON de ejemplo
```

## Cambios realizados sobre el proyecto base

- **Actividades:** se agregaron `GET /api/propiedades/tipo/{tipo}` y `GET /api/propiedades/propietario/{idPropietario}`.
- **Cliente:** el `password` se recibe en el JSON pero nunca se devuelve (`@JsonProperty(access = WRITE_ONLY)`);
  con `@JsonIgnore` cualquier `POST /api/clientes` respondía 400.
- **Relaciones LAZY:** como `spring.jpa.open-in-view=false`, se agregó `@EntityGraph` en los repositorios para que
  el JSON no falle con `LazyInitializationException`.
- **Propiedades:** al guardar se valida que el propietario exista.
- **Reservas:** se cargan la unidad y el cliente reales para calcular `importeTotal`; las noches se cuentan por fecha.
- **Pruebas:** `mvnw test` usa H2 en memoria, no necesita MySQL.

## Autor

**Jaqueline López Alvarez** — Ingeniería en Sistemas Computacionales
Asignatura: Arquitectura Orientada a Servicios · Parcial 1
