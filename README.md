# FocusUp - Aplicación de Gestión de Horarios y Tareas

## 📱 Descripción
FocusUp es una aplicación Android desarrollada en Kotlin que permite a estudiantes y trabajadores organizar sus horarios semanales y gestionar tareas con recordatorios. La aplicación utiliza arquitectura MVVM con Jetpack Compose.

## ✨ Características Principales

### 🔐 Autenticación
- Sistema de registro e inicio de sesión
- Validación de credenciales
- Persistencia de sesión con DataStore

### 📅 Gestión de Horarios
- Crear horarios semanales diferenciados para estudiantes y trabajadores
- **Para Trabajadores**: Nombre, descripción, color, horario
- **Para Estudiantes**: Incluye profesor y sala adicional
- Vista semanal del horario
- Edición y eliminación de bloques horarios

### ✅ Gestión de Tareas
- Crear tareas con fecha, hora y descripción
- Adjuntar fotos desde cámara o galería
- Notificaciones programadas
- Marcar tareas como completadas
- Vista de calendario mensual

## 🏗️ Arquitectura

### Patrón MVVM
```
├── data/
│   ├── database/         # Room Database
│   │   ├── entities/     # Entidades de BD
│   │   ├── dao/          # Data Access Objects
│   │   └── FocusUpDatabase.kt
│   └── repository/       # Repositorios
├── domain/
│   └── model/           # Modelos de dominio
├── presentation/
│   ├── screens/         # Pantallas UI
│   ├── viewmodels/      # ViewModels
│   ├── components/      # Componentes reutilizables
│   └── navigation/      # Navegación
└── utils/               # Utilidades
```

## 🛠️ Tecnologías Utilizadas

### Core Android
- **Kotlin** - Lenguaje principal
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño

### Arquitectura
- **MVVM** - Patrón de arquitectura
- **Room** - Base de datos local
- **DataStore** - Persistencia de preferencias
- **Navigation Compose** - Navegación

### Funcionalidades
- **CameraX** - Captura de fotos
- **Coil** - Carga de imágenes
- **WorkManager** - Notificaciones en background
- **Coroutines** - Programación asíncrona

## 📋 Hoja de Ruta de Desarrollo

### ✅ Fase 1: Configuración Base (COMPLETADA)
- [x] Configuración de dependencias
- [x] Estructura MVVM
- [x] Entidades de base de datos
- [x] DAOs y repositorios
- [x] Configuración de permisos
- [x] Utilidades básicas

### 🔄 Fase 2: Autenticación (EN PROGRESO)
- [x] ViewModel de autenticación
- [x] Pantalla de login básica
- [ ] Pantalla de registro
- [ ] Navegación entre pantallas
- [ ] Validaciones completas

### 📅 Fase 3: Funcionalidad Principal
- [ ] Pantalla de selección de perfil (Estudiante/Trabajador)
- [ ] Creación y edición de horarios
- [ ] Vista semanal del horario
- [ ] Pantalla de calendario
- [ ] Gestión de tareas

### 🚀 Fase 4: Características Avanzadas
- [ ] Integración de cámara/galería
- [ ] Sistema de notificaciones
- [ ] Configuraciones de usuario
- [ ] Exportar/importar datos

## 🚀 Próximos Pasos

### Inmediatos:
1. **Completar pantalla de registro**
2. **Implementar navegación con Navigation Compose**
3. **Crear pantalla de selección de perfil**
4. **Desarrollar formularios de horario**

### Para empezar el desarrollo:

1. **Sincronizar dependencias:**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

2. **Verificar permisos** en AndroidManifest.xml

3. **Continuar con la navegación** entre pantallas

## 🔧 Configuración de Desarrollo

### Dependencias Principales Agregadas:
- Room Database
- Navigation Compose
- ViewModel & LiveData
- CameraX
- Work Manager
- DataStore
- Coil para imágenes

### Permisos Configurados:
- `CAMERA` - Para capturar fotos
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` - Para galería
- `POST_NOTIFICATIONS` - Para notificaciones
- `SCHEDULE_EXACT_ALARM` - Para alarmas precisas
- `VIBRATE` - Para vibración en notificaciones

## 💡 Notas de Implementación

- La contraseña se almacena en texto plano para simplificidad del proyecto universitario
- La aplicación está optimizada para API 24+
- Se utiliza Jetpack Compose para UI moderna
- Arquitectura preparada para escalabilidad

## 📱 Funcionalidades Únicas

1. **Diferenciación automática** entre estudiantes y trabajadores
2. **Horarios con colores personalizables**
3. **Integración de fotos en tareas**
4. **Notificaciones inteligentes**
5. **Vista calendario intuitiva**