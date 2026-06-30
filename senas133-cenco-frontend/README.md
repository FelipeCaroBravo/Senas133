# Señas 133 - Frontend CENCO Simulado

Frontend web en **Angular/Ionic** para simular el panel de CENCO. Se conecta al backend Spring Boot de Señas 133 y permite recibir alertas, verlas en un mapa y cambiar su estado.

## Funciones incluidas

- Barra lateral con alertas activas.
- Estado de cada alerta: enviada, recibida, patrulla despachada, en atención o cerrada.
- Mapa con ubicación recibida desde la app móvil.
- Marcadores por color:
  - Rojo: delitos graves.
  - Naranja: asistencia / vial.
  - Azul: sospecha y prevención.
- Detalle de usuario, dirección, teléfono, tipo de emergencia y precisión GPS.
- Visualización de respuestas del modo camuflaje.
- Acciones CENCO:
  - Recibir alerta.
  - Despachar patrulla.
  - Marcar en atención.
  - Cerrar alerta.
- Asignación simulada de intérprete LSCh.
- Actualización en vivo mediante SSE (`/api/central/stream`) y respaldo con polling cada 8 segundos.

## Requisitos

- Node.js 20+
- Ionic CLI
- Backend Spring Boot ejecutándose en `http://localhost:8080`

Instalar Ionic CLI si no lo tienes:

```bash
npm install -g @ionic/cli
```

## Instalación

```bash
npm install
```

## Ejecución

```bash
ionic serve --port 4200
```

O:

```bash
npm start
```

La aplicación quedará disponible en:

```text
http://localhost:4200
```

## Configurar URL del backend

Modificar:

```text
src/environments/environment.ts
```

Por defecto:

```ts
apiUrl: 'http://localhost:8080/api'
```

Si el backend está en otro computador dentro de la misma red, usa la IP del computador:

```ts
apiUrl: 'http://192.168.1.45:8080/api'
```

## CORS en el backend

En el backend Spring Boot, `application.properties` debe permitir el origen del frontend CENCO:

```properties
app.cors.allowed-origins=http://localhost:4200,http://localhost:8100,http://localhost:5173
server.address=0.0.0.0
```

Si pruebas con IP local:

```properties
app.cors.allowed-origins=http://localhost:4200,http://localhost:8100,http://192.168.1.45:4200,http://192.168.1.45:8100
```

## Endpoints usados

```http
GET   /api/central/emergencias/activas
GET   /api/central/emergencias/{id}
PATCH /api/central/emergencias/{id}/estado
GET   /api/central/emergencias/{id}/ubicaciones
GET   /api/central/emergencias/{id}/contexto
GET   /api/central/stream
GET   /api/central/interpretes/pendientes
PATCH /api/central/emergencias/{id}/interprete/asignar
PATCH /api/central/emergencias/{id}/interprete/iniciar-llamada
```

## Flujo esperado para la prueba

1. Ejecutar el backend Spring Boot.
2. Ejecutar este frontend CENCO en `http://localhost:4200`.
3. Ejecutar la app móvil Ionic o usar `requests.http` para crear una alerta.
4. La alerta aparece en la barra lateral del panel CENCO.
5. El mapa muestra la ubicación recibida.
6. El operador simulado selecciona la alerta y cambia el estado a `RECIBIDA`.
7. La app móvil puede consultar el estado y mostrar “Alerta recibida por la central”.
8. Luego CENCO puede marcar `PATRULLA_DESPACHADA`, `EN_ATENCION` o `CERRADA`.

## Nota sobre el mapa

Este frontend usa **Leaflet + OpenStreetMap**, por lo tanto el computador que muestra el panel necesita internet para cargar los mosaicos del mapa. Aunque no cargue el mapa base, el backend seguirá recibiendo las alertas.
