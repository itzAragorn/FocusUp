@echo off
echo Eliminando cache completo de Gradle...
cd /d "%USERPROFILE%"
rmdir /s /q .gradle 2>nul
echo Cache eliminado completamente.
pause