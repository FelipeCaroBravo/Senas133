# Señas 133 Backend - Spring Boot + MySQL

Backend de prototipo funcional para **Señas 133: Emergencias sin Barreras**.

Incluye el flujo actualizado del mockup:

- Clave Única simulada.
- Acceso rápido por PIN.
- Alerta roja inmediata a CENCO simulado.
- Alertas naranja/azul con detalle antes del envío.
- CENCO simulado como frontend separado.
- GPS y actualizaciones de ubicación.
- Intérprete LSCh simulado.
- SMS de contingencia.
- **Modo camuflaje actualizado**: puzzle → preguntas ocultas → envío de alerta → “Calculando puntaje” → puntaje al confirmar CENCO.

## Requisitos

- Java 21
- Maven
- Docker Desktop
- MySQL 8 si no usas Docker

## Ejecutar solo MySQL con Docker y backend local

```bash
docker compose up -d mysql
mvn spring-boot:run
```

Backend:

```text
http://localhost:8080
```

## Ejecutar backend + MySQL con Docker

```bash
docker compose up --build
```

## Ejecutar backend + MySQL + frontends con Docker

Este ZIP incluye los archivos Docker para los frontends, pero **no incluye ni recrea** las carpetas de los frontends.

La estructura esperada es:

```text
proyecto-senas133/
├── senas133-backend-mysql/
├── senas133-app-movil/
└── senas133-cenco-frontend/
```

Desde `senas133-backend-mysql/` ejecuta:

```bash
docker compose -f docker-compose.full.yml up --build
```

Servicios:

```text
Backend:       http://localhost:8080
App móvil web: http://localhost:8100
CENCO web:     http://localhost:4200
MySQL:         localhost:3306
```

## Base de datos

La base de datos se llama:

```text
mediacion_directa
```

Credenciales Docker:

```text
usuario: senas_user
clave:   senas_pass
root:    rootpass
```

El script con tablas y datos de prueba está en:

```text
Scripts/schema-data.sql
```

Datos demo:

```text
Usuario completo:
RUT: 11111111-1
PIN: 1234

Usuario camuflaje:
RUT: 33333333-3
PIN: 1234
```

## Modo camuflaje actualizado

Flujo:

```text
Pantalla inicio → Camuflaje → arrastrar pieza → preguntas inferiores → enviar alerta a CENCO → “Calculando puntaje” → CENCO confirma → puntaje obtenido
```

Endpoints principales:

```http
GET  /api/camuflaje/configuracion
POST /api/camuflaje/emergencias
GET  /api/camuflaje/emergencias/{id}/resultado
```

Ejemplo de creación de alerta camuflada:

```json
{
  "usuarioId": 1,
  "latitud": -36.828,
  "longitud": -73.051,
  "precisionMetros": 12,
  "respuestas": [
    { "codigoPregunta": "AGRESOR_CERCA", "respuesta": true },
    { "codigoPregunta": "HAY_ARMA", "respuesta": false },
    { "codigoPregunta": "NECESITA_AYUDA_INMEDIATA", "respuesta": true }
  ]
}
```

Mientras CENCO no responde:

```json
{
  "confirmadaPorCenco": false,
  "puntaje": null,
  "mensajePantalla": "Calculando puntaje..."
}
```

Cuando CENCO cambia la emergencia a `RECIBIDA`:

```json
{
  "confirmadaPorCenco": true,
  "puntaje": 11500,
  "mensajePantalla": "Puntaje obtenido"
}
```

## CENCO simulado

Endpoints usados por el frontend CENCO:

```http
GET   /api/central/emergencias/activas
GET   /api/central/emergencias/{id}
PATCH /api/central/emergencias/{id}/estado
GET   /api/central/emergencias/{id}/ubicaciones
GET   /api/central/emergencias/{id}/contexto
GET   /api/central/stream
```

## SMS de contingencia

El número receptor se configura en:

```properties
app.sms.center-phone=${SMS_CENTER_PHONE:+56900000000}
```

O en Docker:

```yaml
SMS_CENTER_PHONE: +56900000000
```

Para pruebas cambia `+56900000000` por el número que recibirá los SMS.

## Probar endpoints

El archivo:

```text
requests.http
```

incluye pruebas para Clave Única simulada, PIN, alertas, CENCO, modo camuflaje, intérprete y SMS.
