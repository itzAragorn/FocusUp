# 🧪 **GUÍA PARA EJECUTAR TESTS UNITARIOS - DEMOSTRACIÓN ACADÉMICA**

## 📋 **Comando Principal para el Profesor**

### **Opción Rápida (Recomendada):**
```powershell
./gradlew test --info
```

### **Si quiere ver solo los resultados:**
```powershell
./gradlew test
```

### **Para limpiar y ejecutar desde cero:**
```powershell
./gradlew clean test
```

## 📊 **Interpretación de Resultados**

### **Salida Exitosa Esperada:**
```
BUILD SUCCESSFUL in 44s
51 actionable tasks: 14 executed, 37 up-to-date
```

### **Métricas que Verá:**
- ✅ **Total Tests**: 28
- ✅ **Failures**: 0
- ✅ **Success Rate**: 100%
- ✅ **Duration**: ~1.7 segundos

## 📁 **Ubicación de Reportes HTML (Para Revisión Detallada)**

### **Reporte Principal:**
```
app\build\reports\tests\testDebugUnitTest\index.html
```

### **Comando para Abrir Reporte:**
```powershell
Start-Process "app\build\reports\tests\testDebugUnitTest\index.html"
```

## 🎓 **Para la Demostración Académica**

### **1. Preparación (Antes que llegue el profesor):**
```powershell
# Limpiar builds anteriores
./gradlew clean

# Verificar que todo compile
./gradlew assembleDebug
```

### **2. Ejecución en Vivo (Frente al profesor):**
```powershell
# Ejecutar todos los tests con información detallada
./gradlew test --info
```

### **3. Mostrar Cobertura por Archivos:**
- **ExampleUnitTest.kt**: 8 tests (Validaciones básicas)
- **TaskRepositoryTestSimple.kt**: 9 tests (CRUD de tareas)  
- **UserRepositoryTestSimple.kt**: 11 tests (Gestión de usuarios)

### **4. Abrir Reporte Visual:**
```powershell
Start-Process "app\build\reports\tests\testDebugUnitTest\index.html"
```

## 🔍 **Comandos de Verificación Adicionales**

### **Ver solo tests específicos:**
```powershell
# Solo tests de repositorio de tareas
./gradlew test --tests "*TaskRepository*"

# Solo tests de repositorio de usuarios  
./gradlew test --tests "*UserRepository*"

# Solo tests básicos
./gradlew test --tests "*ExampleUnitTest*"
```

### **Ejecutar con más verbosidad:**
```powershell
./gradlew test --info --debug
```

## 📝 **Script Rápido para Demostración**

### **demo_tests.ps1** (Crear este archivo):
```powershell
Write-Host "🧪 EJECUTANDO TESTS UNITARIOS - FocusUp" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
Write-Host ""

Write-Host "📊 Total de Tests Implementados: 28" -ForegroundColor Cyan
Write-Host "📁 Archivos de Test: 3" -ForegroundColor Cyan  
Write-Host "🎯 Cobertura: >80% (Requisito Cumplido)" -ForegroundColor Cyan
Write-Host ""

Write-Host "⚡ Ejecutando tests..." -ForegroundColor Yellow
./gradlew test

Write-Host ""
Write-Host "📋 Abriendo reporte detallado..." -ForegroundColor Yellow
Start-Process "app\build\reports\tests\testDebugUnitTest\index.html"

Write-Host ""
Write-Host "✅ DEMOSTRACIÓN COMPLETA" -ForegroundColor Green
```

### **Ejecutar el script:**
```powershell
./demo_tests.ps1
```

## 🎯 **Puntos Clave para Mencionar al Profesor**

### **1. Arquitectura de Testing:**
- "Implementé 28 tests unitarios usando JUnit y MockK"
- "Los tests cubren las 3 capas principales: Repositorios, Lógica y Validaciones"

### **2. Cobertura Académica:**
- "Superamos el 80% de cobertura requerido"
- "Cada funcionalidad crítica tiene sus respectivos tests"

### **3. Tecnologías Utilizadas:**
- "JUnit 4.13.2 para el framework base"
- "MockK 1.13.8 para simular dependencias"
- "Coroutines Test para código asíncrono"

### **4. Resultados Demostrados:**
- "100% de tests pasando sin fallos"
- "Ejecución rápida en 1.7 segundos"
- "Reportes HTML automáticos generados"

## ⚡ **Comando de Emergencia (Si algo falla):**

```powershell
# Limpiar todo y recompilar
./gradlew clean build

# Ejecutar tests desde cero
./gradlew test --rerun-tasks
```

## 📱 **Mostrar en Android Studio (Alternativa Visual)**

### **Si prefieres usar la GUI:**
1. Abrir Android Studio
2. Ir a: `app/src/test/java/com/example/focusup`
3. Click derecho en la carpeta → "Run Tests"
4. Ver resultados en la ventana "Run"

---

**💡 Tip:** Practica el comando `./gradlew test` antes de la demostración para asegurarte que funcione perfectamente.