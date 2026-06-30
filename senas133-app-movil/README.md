# Señas 133 - App móvil Ionic/Angular

Frontend móvil para el prototipo **Señas 133 / Mediación Directa**, construido con **Ionic + Angular + Capacitor**.

El diseño está basado en los mockups subidos: inicio, Clave Única simulada, perfil, PIN, pantalla principal de alertas, flujo de alerta roja inmediata, selector de detalles, confirmación, videollamada con intérprete y modo camuflaje tipo puzzle.

## Requisitos

- Node.js 20+
- Ionic CLI
- Angular CLI
- Backend Spring Boot activo

```bash
npm install -g @ionic/cli @angular/cli
npm install
ionic serve
```

La app abrirá normalmente en:

```text
http://localhost:8100
```

## Configurar backend

Edita:

```text
src/environments/environment.ts
```

Por defecto viene así:

```ts
apiUrl: 'http://localhost:8080/api'
```

Si pruebas desde un teléfono real, debes cambiar `localhost` por la IP del computador donde corre Spring Boot:

```ts
apiUrl: 'http://192.168.1.45:8080/api'
```

En el backend, recuerda permitir CORS para:

```text
http://localhost:8100
http://192.168.1.45:8100
```

## Flujo implementado

### Inicio

- Botón **Ingresar con PIN**.
- Link **Nuevo usuario? Ingresa con Clave Única**.
- Ícono fantasma para entrar al modo camuflaje.

### Registro

- Clave Única se simula llamando al backend.
- Luego se completa dirección, teléfono y PIN.
- El usuario queda guardado localmente en el dispositivo con Capacitor Preferences.

### PIN

- Permite ingresar con PIN de 4 dígitos.
- Después envía a la pantalla principal de emergencias.

### Emergencias

- Botón rojo central: envía alerta inmediata sin detalle.
- Alerta naranja y azul: primero piden detalle y después envían.
- Incluye acceso a perfil y tutorial visual.

### Alerta roja

1. Se presiona el botón rojo.
2. La app envía ubicación y alerta silenciosa al backend.
3. Se muestra pantalla esperando confirmación de CENCO.
4. Cuando CENCO marca la emergencia como `RECIBIDA`, la app muestra confirmación.
5. Pregunta si el usuario puede entregar más información.
6. Si responde sí, permite seleccionar: robo con violencia, asalto en curso o agresión física.
7. Luego muestra la pantalla de alerta activada y botón para intérprete.

### Alertas naranja y azul

- El usuario selecciona primero el subtipo.
- Se envía la emergencia con detalle.
- Luego aparece la pantalla de alerta activa.

### Modo camuflaje

1. Se entra desde el fantasma de la pantalla inicial.
2. La pantalla simula un puzzle.
3. Al arrastrar/tocar la pieza aparecen preguntas camufladas.
4. Al terminar las respuestas, la app crea una emergencia camuflada.
5. Aparece interfaz de carga: **Calculando puntaje**.
6. Cuando CENCO confirma la emergencia, aparece **Nivel completado** y el puntaje.

## Endpoints esperados del backend

```http
POST /api/auth/clave-unica/mock
POST /api/usuarios/{id}/completar-perfil
POST /api/auth/login-pin
GET  /api/usuarios/{id}
PATCH /api/usuarios/{id}/perfil
PATCH /api/usuarios/{id}/pin

POST /api/emergencias
GET  /api/emergencias/{id}
PATCH /api/emergencias/{id}/detalle
POST /api/emergencias/{id}/ubicacion
POST /api/emergencias/{id}/interprete/solicitar

GET  /api/camuflaje/configuracion
POST /api/camuflaje/emergencias
GET  /api/camuflaje/emergencias/{id}/resultado
```

## Generar APK Android

```bash
ionic build
npx cap add android
npx cap sync android
npx cap open android
```

Desde Android Studio puedes generar la APK.

## Nota

Este frontend está preparado para prototipo funcional. La videollamada y Clave Única están tratadas como flujos simulados, porque la integración real requiere servicios externos oficiales.
