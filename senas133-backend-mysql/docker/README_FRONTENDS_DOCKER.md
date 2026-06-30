# Docker para frontends Ionic/Angular

Este backend incluye archivos Docker para levantar también los frontends, pero **no incluye ni recrea** las carpetas de frontend.

Estructura esperada:

```text
proyecto-senas133/
├── senas133-backend-mysql/
├── senas133-app-movil/
└── senas133-cenco-frontend/
```

Desde `senas133-backend-mysql/` puedes levantar todo con:

```bash
docker compose -f docker-compose.full.yml up --build
```

Puertos:

```text
Backend:       http://localhost:8080
App móvil web: http://localhost:8100
CENCO web:     http://localhost:4200
MySQL:         localhost:3306
```

Para probar desde teléfono real debes cambiar `environment.ts` de la app móvil y usar la IP del computador en vez de `localhost`.
