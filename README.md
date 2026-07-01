# Señas 133 / Mediación Directa

Prototipo funcional de una aplicación móvil de asistencia digital para personas sordas, orientada al envío de alertas a una central CENCO simulada. El sistema permite registrar un usuario mediante Clave Única simulada, acceder con PIN, enviar alertas con ubicación, activar modo camuflaje, usar SMS como contingencia y visualizar las alertas desde un frontend CENCO con mapa.

## 1. Estructura del proyecto

La estructura recomendada del proyecto es la siguiente:

```text
senas133/
├── senas133-backend-mysql/
├── senas133-app-movil/
└── senas133-cenco-frontend/
```

Cada carpeta cumple una función distinta:

```text
senas133-backend-mysql     Backend Spring Boot + MySQL
senas133-app-movil         Frontend móvil Ionic/Angular
senas133-cenco-frontend    Frontend web CENCO simulado
```

## 2. Tecnologías utilizadas

### Backend

```text
Java 17+
Spring Boot
Spring Data JPA
MySQL
Maven Wrapper / Maven
Docker
```

### Frontend móvil

```text
Angular
Ionic
Capacitor
TypeScript
```

### Frontend CENCO

```text
Angular
Ionic
Leaflet
OpenStreetMap
TypeScript
```

### Base de datos

```text
MySQL 8
```

## 3. Programas necesarios

### Para ejecutar con Docker

Para la forma más simple de ejecución se necesita:

```text
Docker Desktop
Docker Compose
```

Con Docker no es necesario instalar manualmente Maven ni MySQL, porque los contenedores se encargan de levantar esos servicios.

### Para ejecutar sin Docker

Para ejecutar el proyecto sin Docker se necesita instalar:

```text
Java JDK 17 o superior
Node.js 20 o superior
npm
Ionic CLI
MySQL Server 8
Visual Studio Code o IDE similar
```

Instalar Ionic CLI:

```bash
npm install -g @ionic/cli
```

El backend puede ejecutarse con **Maven Wrapper**, por lo que no es obligatorio tener Maven instalado globalmente si el repositorio incluye estos archivos:

```text
mvnw
mvnw.cmd
.mvn/
```

En Windows se usa:

```cmd
.\mvnw.cmd spring-boot:run
```

En Linux/macOS se usa:

```bash
./mvnw spring-boot:run
```

Si el proyecto todavía no tiene Maven Wrapper, se debe instalar Maven una vez y generarlo con:

```cmd
mvn -N wrapper:wrapper
```

### Opcional para generar APK

```text
Android Studio
Capacitor
```

## 4. Ejecución con Docker

### 4.1. Ejecutar solo backend y MySQL

Entrar a la carpeta del backend:

```bash
cd senas133-backend-mysql
```

Ejecutar:

```bash
docker compose up --build
```

Esto levanta:

```text
MySQL:   localhost:3306
Backend: http://localhost:8080
```

### 4.2. Ejecutar backend, MySQL, app móvil y CENCO

La estructura debe ser:

```text
senas133/
├── senas133-backend-mysql/
├── senas133-app-movil/
└── senas133-cenco-frontend/
```

Entrar a la carpeta del backend:

```bash
cd senas133-backend-mysql
```

Ejecutar:

```bash
docker compose -f docker-compose.full.yml up --build
```

Esto levanta:

```text
Backend:       http://localhost:8080
App móvil:     http://localhost:8100
CENCO:         http://localhost:4200
MySQL:         localhost:3306
```

### 4.3. Reiniciar base de datos desde cero

Si se desea borrar la base de datos y volver a cargar los datos de prueba:

```bash
docker compose -f docker-compose.full.yml down -v
docker compose -f docker-compose.full.yml up --build
```

El parámetro `-v` elimina el volumen de MySQL.

## 5. Ejecución sin Docker

### 5.1. Crear base de datos MySQL

Entrar a MySQL:

```bash
mysql -u root -p
```

Crear base de datos y usuario:

```sql
CREATE DATABASE mediacion_directa;
CREATE USER 'senas_user'@'localhost' IDENTIFIED BY 'senas_pass';
GRANT ALL PRIVILEGES ON mediacion_directa.* TO 'senas_user'@'localhost';
FLUSH PRIVILEGES;
```

Salir de MySQL:

```sql
EXIT;
```

### 5.2. Cargar datos de prueba

Desde la carpeta del backend:

```bash
cd senas133-backend-mysql
```

Ejecutar:

```bash
mysql -u senas_user -p mediacion_directa < Scripts/schema-data.sql
```

Contraseña:

```text
senas_pass
```

### 5.3. Ejecutar backend

Desde la carpeta del backend:

```bash
cd senas133-backend-mysql
```

#### En Windows

```cmd
.\mvnw.cmd spring-boot:run
```

#### En Linux/macOS

```bash
./mvnw spring-boot:run
```

#### Alternativa con Maven instalado globalmente

```bash
mvn spring-boot:run
```

El backend queda disponible en:

```text
http://localhost:8080
```

Si aparece el mensaje `mvn no se reconoce como un comando interno o externo`, significa que Maven no está instalado o no está agregado al PATH. En ese caso, usar Maven Wrapper:

```cmd
.\mvnw.cmd spring-boot:run
```

Si el proyecto aún no tiene `mvnw.cmd`, instalar Maven y generar el wrapper con:

```cmd
mvn -N wrapper:wrapper
```

### 5.4. Ejecutar app móvil

En otra terminal:

```bash
cd senas133-app-movil
npm install
ionic serve --port 8100
```

Si Ionic da problemas con los argumentos de puerto, usar:

```bash
npx ng serve --port 8100
```

Abrir:

```text
http://localhost:8100
```

### 5.5. Ejecutar CENCO simulado

En otra terminal:

```bash
cd senas133-cenco-frontend
npm install
ionic serve --port 4200
```

Si Ionic da problemas:

```bash
npx ng serve --port 4200
```

Abrir:

```text
http://localhost:4200
```

## 6. Maven Wrapper

El Maven Wrapper permite ejecutar el backend sin depender de una instalación global de Maven.

Para generarlo por primera vez, desde la carpeta del backend:

```cmd
cd senas133-backend-mysql
mvn -N wrapper:wrapper
```

Esto genera:

```text
mvnw
mvnw.cmd
.mvn/
```

Estos archivos deben mantenerse en el repositorio para facilitar la ejecución del proyecto por otras personas.

Después de generarlo, el backend se puede ejecutar con:

```cmd
.\mvnw.cmd spring-boot:run
```

## 7. Rutas principales para probar

### Backend

```text
http://localhost:8080/api/catalogo/emergencias
http://localhost:8080/api/camuflaje/configuracion
http://localhost:8080/api/central/emergencias/activas
```

La ruta:

```text
http://localhost:8080
```

puede mostrar un error tipo `No static resource`, lo cual es normal porque el backend es una API REST y no una página web.

### App móvil

```text
http://localhost:8100
```

### CENCO simulado

```text
http://localhost:4200
```

## 8. Flujo básico de prueba

1. Abrir la app móvil:

```text
http://localhost:8100
```

2. Entrar con Clave Única simulada o completar perfil.

3. Crear PIN de acceso rápido.

4. Ingresar con PIN.

5. Enviar alerta.

6. Abrir CENCO:

```text
http://localhost:4200
```

7. Ver la alerta recibida en la barra lateral.

8. Ver ubicación en el mapa.

9. Cambiar estado de la alerta a:

```text
RECIBIDA
PATRULLA_DESPACHADA
EN_ATENCION
CERRADA
```

10. Volver a la app móvil y verificar que cambie el estado.

## 9. Flujo del modo camuflaje

El modo camuflaje funciona así:

```text
Pantalla de inicio
→ Botón camuflaje
→ Puzzle
→ Arrastrar pieza
→ Aparecen preguntas literales
→ Al responder se envía alerta a CENCO
→ Se muestra “Calculando puntaje”
→ Cuando CENCO confirma, aparece el puntaje obtenido
```

Endpoints principales:

```text
GET  /api/camuflaje/configuracion
POST /api/camuflaje/emergencias
GET  /api/camuflaje/emergencias/{id}/resultado
```

## 10. Partes del código que se deben cambiar para pruebas

### 10.1. Cambiar IP del backend para probar desde celular

Si todo se prueba desde el mismo computador, usar:

```text
localhost
```

Si se prueba desde un teléfono real conectado a la misma red WiFi, no usar `localhost`. Se debe usar la IP del computador.

En Windows, obtener IP con:

```bash
ipconfig
```

Buscar:

```text
Dirección IPv4
```

Ejemplo:

```text
192.168.1.45
```

### App móvil

Archivo:

```text
senas133-app-movil/src/environments/environment.ts
```

Para localhost:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  pollingMs: 3000
};
```

Para teléfono real:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://192.168.1.45:8080/api',
  pollingMs: 3000
};
```

También revisar:

```text
senas133-app-movil/src/environments/environment.prod.ts
```

si se hará build o APK.

Importante: si `angular.json` no tiene configurado `fileReplacements`, el build puede seguir usando `environment.ts`. En ese caso, para pruebas de APK se recomienda cambiar también `environment.ts`.

### CENCO simulado

Archivo:

```text
senas133-cenco-frontend/src/environments/environment.ts
```

Para localhost:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  pollingMs: 3000
};
```

Para red local:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://192.168.1.45:8080/api',
  pollingMs: 3000
};
```

### 10.2. Usar ngrok o cloudflared

Si se quiere probar la APK desde otra red o usando datos móviles, se puede exponer el backend local con una URL pública temporal.

Con cloudflared:

```bash
cloudflared tunnel --url http://localhost:8080
```

Ejemplo de URL generada:

```text
https://ejemplo.trycloudflare.com
```

Con ngrok:

```bash
ngrok http 8080
```

Ejemplo de URL generada:

```text
https://ejemplo.ngrok-free.app
```

Luego cambiar en la app móvil:

```ts
export const environment = {
  production: false,
  apiUrl: 'https://ejemplo.trycloudflare.com/api',
  pollingMs: 3000
};
```

Para APK, revisar también:

```text
senas133-app-movil/src/environments/environment.prod.ts
```

### 10.3. Cambiar CORS en el backend

Archivo:

```text
senas133-backend-mysql/src/main/resources/application.properties
```

Para localhost:

```properties
app.cors.allowed-origins=http://localhost:4200,http://localhost:8100,http://localhost:5173,http://localhost:3000
```

Para probar desde celular con IP local:

```properties
app.cors.allowed-origins=http://localhost:4200,http://localhost:8100,http://192.168.1.45:4200,http://192.168.1.45:8100
```

Para pruebas con APK/Capacitor, agregar también:

```properties
capacitor://localhost,ionic://localhost,http://localhost,https://localhost
```

Ejemplo recomendado:

```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:4200,http://localhost:5173,http://localhost:3000,http://localhost:8100,http://127.0.0.1:4200,http://127.0.0.1:8100,capacitor://localhost,ionic://localhost,http://localhost,https://localhost}
```

El backend debe tener:

```properties
server.address=0.0.0.0
server.port=8080
```

### 10.4. Cambiar número que recibe SMS

Archivo:

```text
senas133-backend-mysql/src/main/resources/application.properties
```

Buscar:

```properties
app.sms.center-phone=+56900000000
```

Cambiar por el número que recibirá los SMS durante la prueba:

```properties
app.sms.center-phone=+56912345678
```

Si se usa Docker, revisar también:

```text
senas133-backend-mysql/docker-compose.yml
senas133-backend-mysql/docker-compose.full.yml
```

Buscar:

```yaml
SMS_CENTER_PHONE: +56900000000
```

Cambiar por:

```yaml
SMS_CENTER_PHONE: +56912345678
```

### 10.5. Configuración de base de datos

Archivo:

```text
senas133-backend-mysql/src/main/resources/application.properties
```

Configuración local esperada:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mediacion_directa?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago
spring.datasource.username=senas_user
spring.datasource.password=senas_pass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Si se usa Docker, la URL debe apuntar al servicio MySQL:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/mediacion_directa?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago}
```

Y en Docker Compose:

```yaml
DB_URL: jdbc:mysql://mysql:3306/mediacion_directa?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago
DB_USERNAME: senas_user
DB_PASSWORD: senas_pass
```