@echo off
setlocal

REM Ajusta esta contraseña si tu usuario postgres usa otra.
set PGPASSWORD=1234

REM Cambia la ruta si tienes otra versión de PostgreSQL.
set PSQL="C:\Program Files\PostgreSQL\13\bin\psql.exe"

if not exist %PSQL% (
  echo No se encontro psql en la ruta configurada.
  echo Edita este archivo y cambia la variable PSQL.
  pause
  exit /b 1
)

echo Eliminando base de datos anterior si existe...
%PSQL% -U postgres -h localhost -p 5432 -d postgres -c "DROP DATABASE IF EXISTS mediacion_directa;"

echo Creando base de datos mediacion_directa...
%PSQL% -U postgres -h localhost -p 5432 -d postgres -c "CREATE DATABASE mediacion_directa;"

echo Creando tablas e insertando datos de prueba...
%PSQL% -U postgres -h localhost -p 5432 -d mediacion_directa -f "%~dp0schema-data.sql"

echo Proceso terminado.
pause
