# Señas 133 — Backend Spring Boot

Backend de prototipo funcional para **Señas 133: Emergencias sin Barreras**. El proyecto representa la capa servidor de una aplicación móvil para comunidad sorda, con envío de alertas, ubicación GPS, modo camuflaje, contingencia SMS, chat, solicitud de intérprete LSCh y panel de central simulado.

> Este backend es un prototipo académico. No se conecta realmente con Carabineros, CENCO ni proveedores SMS. La integración externa y SMS quedan simuladas o preparadas para ser reemplazadas.

## Tecnologías

- Java 21
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Bean Validation
- BCrypt para PIN

## Funcionalidades incluidas

### Usuario y acceso seguro

- Registro con nombre, RUT, número de documento, teléfono y PIN.
- Login con PIN.
- Sesión temporal en memoria mediante token Bearer.

### Emergencias

- Creación de alerta con categoría cromática:
  - `DELITOS_GRAVES`
  - `ASISTENCIA_VIAL`
  - `SOSPECHA_PREVENCION`
- Subtipo de incidente, por ejemplo `ROBO`, `ACCIDENTE_TRANSITO`, `VIOLENCIA_INTRAFAMILIAR`.
- Envío de ubicación inicial.
- Actualización de ubicación periódica.
- Estado de emergencia para central: `ENVIADA`, `RECIBIDA`, `EN_ATENCION`, `CERRADA`, etc.

### Modo camuflaje

- Endpoint de configuración para que el frontend muestre un puzzle/rompecabezas.
- Creación de emergencia desde modo camuflaje.
- Registro de respuestas binarias ocultas, por ejemplo:
  - agresor armado
  - agresor presente
  - víctima herida
  - menores presentes
  - salida bloqueada
- Visualización de contexto oculto desde la central.

### SMS de contingencia

- El backend entrega configuración de número destino.
- El backend prepara el texto del SMS.
- La app móvil debe abrir la app Mensajes del teléfono con el texto listo.
- Cuando vuelve internet, la app sincroniza el SMS enviado con el backend.

Importante: si el teléfono no tiene internet, no puede contactar al backend. El SMS se envía desde el teléfono del usuario hacia el número definido como central.

### Intérprete LSCh

- Solicitud de intérprete asociada a una emergencia.
- Asignación simulada de intérprete y URL de sala de videollamada.
- Cambio de estado de la solicitud.

### Chat y eventos en vivo

- Chat simple asociado a la emergencia.
- Stream SSE para que el panel central reciba eventos en vivo:

```http
GET /api/central/stream
```

## Estructura principal

```text
src/main/java/com/mediaciondirecta/
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── integration
├── repository
└── service
```

## Crear base de datos manualmente

El proyecto está configurado para que Spring Boot **no cree las tablas automáticamente**:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Para crear la base de datos en Windows:

1. Revisa `Scripts/crear_db.bat` y ajusta la ruta de `psql.exe` si es necesario.
2. Revisa la contraseña del usuario `postgres`.
3. Ejecuta:

```cmd
Scripts\crear_db.bat
```

El script crea la base:

```text
mediacion_directa
```

y carga datos de prueba.

Usuarios de prueba:

```text
RUT: 12345678-9
PIN: 1234

RUT: 11111111-1
PIN: 1234
```

## Configuración

Archivo principal:

```text
src/main/resources/application.properties
```

Propiedades importantes:

```properties
server.port=8080
server.address=0.0.0.0
spring.datasource.url=jdbc:postgresql://localhost:5432/mediacion_directa
spring.datasource.username=postgres
spring.datasource.password=1234
app.sms.center-phone=+56900000000
```

Cambia `app.sms.center-phone` por el número de teléfono que actuará como central simulada.

## Ejecutar

```bash
mvn spring-boot:run
```

## Probar endpoints

Usa el archivo:

```text
requests.http
```

Flujo sugerido:

1. Login con PIN.
2. Copiar el token recibido.
3. Reemplazar `@token` en `requests.http`.
4. Crear emergencia normal o camuflada.
5. Consultar `/api/central/emergencias/activas`.
6. Guardar respuestas ocultas.
7. Preparar SMS.
8. Solicitar intérprete.

## Endpoints principales

```http
POST /api/auth/registro
POST /api/auth/login-pin
GET  /api/catalogo/emergencias
POST /api/emergencias
POST /api/emergencias/{id}/ubicacion
GET  /api/central/emergencias/activas
GET  /api/central/emergencias/{id}/contexto
GET  /api/central/stream
GET  /api/camuflaje/configuracion
POST /api/camuflaje/emergencias
POST /api/camuflaje/emergencias/{id}/respuestas
GET  /api/sms/configuracion
POST /api/sms/preparar
POST /api/sms/sincronizar
POST /api/emergencias/{id}/interprete/solicitar
PATCH /api/central/emergencias/{id}/interprete/asignar
GET  /api/emergencias/{id}/chat
POST /api/emergencias/{id}/chat
```

## Limitaciones del prototipo

- No usa JWT real; usa tokens en memoria.
- No envía SMS automáticamente.
- No usa videollamada real; guarda una URL simulada.
- No integra un sistema CENCO real; se simula con `SistemaExternoClient`.
- No implementa cifrado de datos sensibles a nivel base de datos.

Para una versión real se debería agregar autenticación robusta, auditoría, cifrado, control de roles, gestión de consentimiento, trazabilidad y validación legal/técnica de la integración externa.
