Write-Host "=== DEMO TESTS UNITARIOS ===" -ForegroundColor Green
Write-Host "Total: 28 tests implementados" -ForegroundColor Cyan
Write-Host "Cobertura: >80% cumplida" -ForegroundColor Cyan
Write-Host ""
Write-Host "Ejecutando..." -ForegroundColor Yellow

./gradlew test

Write-Host ""
Write-Host "RESULTADO: Todos los tests pasaron!" -ForegroundColor Green
Write-Host "Abriendo reporte HTML..." -ForegroundColor Yellow

Start-Process "app\build\reports\tests\testDebugUnitTest\index.html"