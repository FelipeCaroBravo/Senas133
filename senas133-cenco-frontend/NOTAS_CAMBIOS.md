# Notas de integración con el backend

Este frontend está pensado para trabajar con el backend `senas133-backend-mysql-modificado`.

No requiere cambios grandes en el backend. Solo revisar:

1. CORS debe permitir `http://localhost:4200`.
2. El backend debe exponer `/api/central/emergencias/activas`.
3. Las emergencias deben traer `latitud`, `longitud` y `precisionMetros`.
4. Para el flujo en vivo, el backend debe mantener activo `/api/central/stream`.
5. Para el modo camuflaje, CENCO leerá respuestas desde `/api/central/emergencias/{id}/contexto`.
