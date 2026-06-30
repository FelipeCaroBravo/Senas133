@echo off
REM Reinicia MySQL Docker y vuelve a cargar schema-data.sql.
cd /d %~dp0\..
docker compose down -v
docker compose up -d mysql
echo Espera unos segundos mientras MySQL inicializa y ejecuta el script.
pause
