@echo off
echo Limpiando cache de Gradle...
cd /d "%USERPROFILE%\.gradle\caches\8.13"
rmdir /s /q transforms 2>nul
echo Cache limpiado.
pause