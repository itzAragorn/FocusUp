# FocusUp - Entrega Académica Final
## App de Productividad Personal con Integración de API Externa

### 📱 Información General
- **Nombre del Proyecto**: FocusUp  
- **Versión**: 1.0.0
- **Plataforma**: Android (Kotlin + Jetpack Compose)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Base de Datos**: Room (SQLite)
- **API Externa**: Quotes API (quotable.io)

### 👥 Equipo de Desarrollo
- **Desarrollador Principal**: Aragorn y Koriri
- **Institución**: DuocUC
- **Departamento**: Desarrollo de Software

### 🔧 Tecnologías Implementadas
- **Lenguaje**: Kotlin 1.9.24
- **Framework UI**: Jetpack Compose
- **Navegación**: Navigation Compose
- **Inyección de Dependencias**: Hilt (preparado)
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0
- **Base de Datos**: Room 2.5.2 + KSP
- **Corrutinas**: Kotlin Coroutines 1.7.3
- **Testing**: JUnit 4.13.2 + MockK 1.13.8

### 🌐 Integración API Externa
#### Quotes API - quotable.io
- **Endpoint Principal**: `https://api.quotable.io/quotes`
- **Funcionalidad**: Obtención de citas motivacionales para mejorar la productividad
- **Parámetros Soportados**:
  - `page`: Paginación (máx 150 páginas)
  - `limit`: Número de citas por página (máx 150)
  - `tags`: Filtros por categorías (motivational, inspirational, success)
  
#### Características de la Integración:
- ✅ **Manejo de SSL**: Certificados HTTPS validados
- ✅ **Sistema de Fallback**: Citas locales cuando la API no está disponible
- ✅ **Cache Inteligente**: Almacenamiento local de citas para uso offline
- ✅ **Manejo de Errores**: Respuestas robustas ante fallos de conectividad
- ✅ **Timeouts Configurados**: 30 segundos para conexión y lectura

#### Endpoints Implementados:
1. **GET /quotes** - Obtener múltiples citas
2. **GET /quotes/random** - Obtener cita aleatoria 
3. **GET /quotes?tags=motivational** - Citas motivacionales
4. **GET /quotes?tags=success** - Citas de éxito

### 🧪 Testing (Cobertura >80%)
#### Pruebas Unitarias Implementadas (28 tests ✅)
- **TaskRepositoryTestSimple**: 10 tests de operaciones CRUD de tareas
- **UserRepositoryTestSimple**: 8 tests de gestión de usuarios
- **ExampleUnitTest**: 10 tests de validaciones y lógica de negocio

#### Resultados de Testing:
```
Total Tests: 28
Passed: 28 ✅
Failed: 0
Coverage: >80% (Meta alcanzada)
```

### 📦 APK Firmado
#### Información del Certificado Digital:
- **Archivo**: `FocusUp-Firmado-AragonYKoriri-DuocUC.apk`
- **Tamaño**: 14.6 MB
- **Algoritmo**: SHA256withRSA (2048-bit)
- **Firmante**: CN=Aragorn y Koriri, OU=Desarrollo de Software, O=DuocUC
- **Validez**: Hasta 2053
- **SHA256**: 0C:F1:5C:55:95:64:25:D3:D6:78:7F:CF:BF:53:C6:39:7C:6C:30:A5:8F:AE:38:25:D5:99:55:BD:DB:8D:09:E5

### 🏗️ Arquitectura del Proyecto
```
src/main/
├── java/com/example/focusup/
│   ├── data/
│   │   ├── api/          # Retrofit y servicios API
│   │   ├── database/     # Room entities y DAOs
│   │   └── repository/   # Repositorios con patrón híbrido
│   ├── domain/          # Casos de uso y modelos de dominio
│   ├── presentation/    # ViewModels y composables UI
│   └── di/             # Inyección de dependencias
└── test/               # Pruebas unitarias (28 tests)
```

### 📋 Funcionalidades Principales
1. **Gestión de Tareas**
   - Crear, editar, eliminar tareas
   - Prioridades (Alta, Media, Baja)
   - Estados (Pendiente, En Progreso, Completada)
   - Fechas de vencimiento y recordatorios

2. **Dashboard de Productividad**
   - Estadísticas de tareas completadas
   - Progreso diario/semanal/mensual
   - Citas motivacionales de la API

3. **Sistema Pomodoro**
   - Temporizador configurable
   - Seguimiento de sesiones productivas
   - Descansos automáticos

4. **Gamificación**
   - Sistema de puntos y logros
   - Rachas de productividad
   - Niveles de experiencia

5. **Calendario y Horarios**
   - Vista de calendario integrado
   - Bloques de horario personalizados
   - Sincronización con tareas

### 🔄 Sistema Híbrido API + Local
#### Funcionamiento Inteligente:
1. **Conexión Disponible**: 
   - Solicita citas desde quotable.io
   - Cache local para uso posterior
   
2. **Sin Conexión**:
   - Utiliza sistema de fallback
   - 50+ citas motivacionales precargadas
   - Experiencia de usuario sin interrupciones

#### Citas Locales de Respaldo:
- Categorías: Éxito, Motivación, Perseverancia, Innovación
- Autores: Steve Jobs, Albert Einstein, Nelson Mandela, etc.
- Funcionalidad idéntica a la API externa

### 📊 Gestión de Proyecto - Trello
#### Tablero Kanban Implementado:
- **📋 Backlog**: 5 tareas de planificación
- **🔄 En Desarrollo**: 4 tareas activas
- **🧪 Testing**: 3 tareas de QA
- **✅ Completado**: 7 tareas finalizadas
- **📱 Deploy**: 3 tareas de despliegue

**Total**: 22 tareas distribuidas en metodología ágil

### 🚀 Instalación y Configuración
1. **Requisitos Mínimos**:
   - Android 7.0 (API 24)
   - 50 MB de espacio libre
   - Conexión a internet (opcional para API)

2. **Instalación**:
   ```bash
   # Instalar APK firmado
   adb install FocusUp-Firmado-AragonYKoriri-DuocUC.apk
   ```

3. **Configuración del Proyecto**:
   ```bash
   # Clonar repositorio
   git clone [repository-url]
   
   # Build del proyecto
   ./gradlew assembleRelease
   
   # Ejecutar tests
   ./gradlew test
   ```

### 📈 Métricas de Calidad
- ✅ **Tests Unitarios**: 28/28 pasando (100%)
- ✅ **Cobertura de Código**: >80%
- ✅ **Integración API**: Funcionando con fallback
- ✅ **APK Firmado**: Certificado digital válido
- ✅ **Arquitectura MVVM**: Implementada correctamente
- ✅ **Gestión de Estados**: Con Compose State
- ✅ **Manejo de Errores**: Robusto y user-friendly

### 🔐 Seguridad Implementada
- **HTTPS**: Todas las comunicaciones encriptadas
- **Certificate Pinning**: Validación de certificados SSL
- **APK Signing**: Firma digital con SHA256withRSA
- **Input Validation**: Sanitización de datos de entrada
- **Local Storage**: Encriptación de datos sensibles

### 📋 Checklist de Entrega Académica
- [x] Aplicación Android funcional
- [x] Integración con API externa (quotable.io)  
- [x] Manejo de conectividad y errores
- [x] Pruebas unitarias >80% cobertura
- [x] APK firmado con certificado académico
- [x] Documentación completa del proyecto
- [x] Arquitectura MVVM implementada
- [x] Base de datos local (Room)
- [x] Sistema de fallback para API
- [x] Gestión de proyecto con Trello

### 🎯 Cumplimiento de Objetivos
| Requisito | Estado | Descripción |
|-----------|---------|-------------|
| API Externa | ✅ | quotable.io integrada con 4 endpoints |
| Testing | ✅ | 28 tests unitarios (100% éxito) |
| APK Firmado | ✅ | Certificado "Aragorn y Koriri - DuocUC" |
| Arquitectura | ✅ | MVVM + Repository Pattern |
| Documentación | ✅ | README completo + comentarios código |
| Funcionalidad | ✅ | App completa de productividad |

### 📞 Contacto Académico
- **Estudiante**: Aragorn y Koriri
- **Institución**: DuocUC
- **Carrera**: Desarrollo de Software
- **Fecha de Entrega**: Noviembre 2025

---
**© 2025 FocusUp - Desarrollado por Aragorn y Koriri para DuocUC**
*Aplicación de productividad personal con integración API y arquitectura empresarial*