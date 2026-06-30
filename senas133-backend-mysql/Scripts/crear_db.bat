@echo off
REM Crea la base de datos MySQL local y carga datos de prueba.
REM Requiere mysql.exe disponible en PATH o ajustar MYSQL_EXE.

cd /d %~dp0
set MYSQL_EXE=mysql
set MYSQL_USER=root
set MYSQL_PASSWORD=rootpass
set MYSQL_HOST=localhost
set MYSQL_PORT=3306

echo Creando base de datos y cargando datos demo...
%MYSQL_EXE% -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% < schema-data.sql

if %ERRORLEVEL% EQU 0 (
  echo Base de datos lista.
) else (
  echo Ocurrio un error. Revisa usuario, password, puerto o ruta de mysql.exe.
)
pause
