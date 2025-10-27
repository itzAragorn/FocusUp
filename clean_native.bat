@echo off
echo Eliminando archivos nativos de Gradle...
cd /d "%USERPROFILE%\.gradle"
rmdir /s /q native 2>nul
echo Archivos nativos eliminados.
pause