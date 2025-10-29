# 🎯 FocusUp - Aplicación de Productividad y Gestión de Tareas

## 📱 Descripción General
FocusUp es una aplicación Android moderna desarrollada en Kotlin con Jetpack Compose que combina gestión de tareas, técnica Pomodoro, horarios semanales y gamificación para maximizar la productividad de estudiantes y trabajadores.

## ✨ Funcionalidades Implementadas

### 🔐 **Sistema de Autenticación Completo**
- Registro e inicio de sesión con validación
- Perfiles diferenciados (Estudiante/Trabajador)
- Persistencia de sesión con DataStore
- Autenticación biométrica opcional
- Pantalla de bloqueo con huella/facial

### 📅 **Gestión de Horarios Inteligente**
- Horarios semanales personalizables por perfil
- **Trabajadores**: Nombre, descripción, color, horario
- **Estudiantes**: Incluye profesor, sala y detalles académicos
- Vista semanal interactiva con colores
- Edición y eliminación en tiempo real

### ✅ **Gestión Avanzada de Tareas**
- Creación con fecha, hora, prioridad y descripción
- Adjuntar fotos desde cámara o galería
- Sistema de notificaciones inteligentes
- Estados: Pendiente, En progreso, Completada
- Vista de calendario mensual con filtros
- Lista de tareas con ordenamiento

### 🍅 **Técnica Pomodoro Integrada**
- Timer personalizable (25/5/15 minutos)
- Notificaciones de inicio/fin de sesión
- Integración con sistema de logros
- Estadísticas de sesiones completadas
- Sonidos y vibraciones configurables

### 📊 **Dashboard de Productividad**
- Estadísticas diarias, semanales y mensuales
- Gráficos de progreso visual
- Métricas de tareas completadas
- Tiempo total de Pomodoro
- Análisis de patrones de productividad

### 🎮 **Sistema de Gamificación Completo**
- **Sistema XP**: Ganar experiencia por actividades
- **Niveles**: Progresión automática con recompensas
- **Logros**: 15+ achievements desbloqueables
- **Notificaciones**: Alertas de nivel y logros
- **Progreso visual**: Barras de XP y estadísticas
- Integración total con todas las funcionalidades

### 👤 **Perfil de Usuario**
- Información personal editable
- Configuración de preferencias
- Estadísticas personales
- Gestión de notificaciones
- Configuración de autenticación biométrica

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

## 📋 Estado Actual del Proyecto

### ✅ **COMPLETADO - Versión 1.0**
- [x] **Arquitectura MVVM completa** con Room Database
- [x] **Sistema de autenticación** con perfiles diferenciados
- [x] **Gestión de horarios** semanales con colores
- [x] **Gestión avanzada de tareas** con fotos y notificaciones
- [x] **Técnica Pomodoro** con timer personalizable
- [x] **Dashboard de estadísticas** con gráficos visuales
- [x] **Sistema de gamificación** (XP, niveles, logros)
- [x] **Perfil de usuario** con configuraciones
- [x] **Navegación completa** entre 13+ pantallas
- [x] **Autenticación biométrica** opcional
- [x] **Notificaciones inteligentes** para todas las funciones
- [x] **Base de datos** con 7 entidades y relaciones
- [x] **9 ViewModels** completamente integrados

### 🏗️ **ARQUITECTURA ACTUAL**
```
├── 7 Entidades Room Database ✅
├── 9 ViewModels integrados ✅
├── 13+ Pantallas con navegación ✅
├── Sistema de gamificación completo ✅
├── Autenticación biométrica ✅
└── Notificaciones para todas las funciones ✅
```

## 🚀 Roadmap de Funcionalidades Futuras

### 📅 **FASE 1: Expansión de Productividad** *(Próximas 2-4 semanas)*
- [ ] **Dashboard de Hábitos**
  - Seguimiento de hábitos diarios (ejercicio, lectura, meditación)
  - Streaks y cadenas de hábitos
  - Integración con sistema de logros existente
- [ ] **Plantillas de Tareas Recurrentes**
  - Tareas que se repiten automáticamente
  - Plantillas personalizables para rutinas
  - Programación inteligente de tareas repetitivas
- [ ] **Modo Focus Profundo**
  - Bloqueo de aplicaciones durante sesiones Pomodoro
  - Sonidos ambientales integrados
  - Técnicas de respiración y relajación

### � **FASE 2: Análisis y Sincronización** *(1-2 meses)*
- [ ] **Sincronización en la Nube**
  - Backup automático de datos
  - Sincronización entre dispositivos
  - Acceso web opcional
- [ ] **Análisis de Productividad con IA**
  - Predicción de momentos más productivos
  - Sugerencias automáticas de horarios óptimos
  - Análisis de patrones de trabajo
- [ ] **Personalización Avanzada**
  - Temas oscuro/claro/personalizado
  - Widgets para pantalla de inicio
  - Configuración de sonidos y vibraciones

### 📅 **FASE 3: Expansión del Ecosistema** *(Futuro a largo plazo)*
- [ ] **Funcionalidades Sociales**
  - Equipos de trabajo o estudio
  - Tareas compartidas y asignadas
  - Desafíos comunitarios y leaderboards
- [ ] **Integraciones Externas**
  - Google Calendar, Notion, Obsidian
  - Spotify para música de focus
  - APIs de productividad populares
- [ ] **Wearables y Multiplataforma**
  - Apple Watch / Wear OS support
  - Control remoto de Pomodoro
  - Expansión a iOS y Web

## 🎯 **Próximos Pasos Recomendados**

### Para continuar el desarrollo:

1. **Compilar y probar la versión actual:**
   ```bash
   ./gradlew clean assembleDebug
   ```

2. **Elegir una funcionalidad de la Fase 1** para implementar

3. **Mantener la arquitectura MVVM** existente que está funcionando perfectamente

## 🔧 Stack Tecnológico Completo

### **Lenguajes y Frameworks**
- **Kotlin** - Lenguaje principal con corrutinas
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño consistente

### **Arquitectura y Patrones**
- **MVVM** - Patrón de arquitectura implementado
- **Room Database v2.6.1** - Base de datos local con KSP
- **Repository Pattern** - Abstracción de datos
- **Dependency Injection** - Manual, optimizada para el proyecto

### **Persistencia y Datos**
- **Room Database** - 7 entidades con relaciones
- **DataStore Preferences** - Preferencias de usuario
- **SharedPreferences** - Configuraciones rápidas
- **File Storage** - Almacenamiento de imágenes

### **UI y Navegación**
- **Navigation Compose v2.8.3** - Navegación declarativa
- **Compose BOM 2024.09.00** - UI components
- **Material Icons Extended** - Iconografía completa
- **CameraX v1.4.0** - Integración de cámara
- **Coil v2.7.0** - Carga optimizada de imágenes

### **Funcionalidades Avanzadas**
- **WorkManager v2.9.1** - Notificaciones en background
- **Biometric Authentication** - Autenticación biométrica
- **Coroutines v1.7.3** - Programación asíncrona
- **KSP v2.0.21** - Procesamiento de anotaciones

### **Configuración de Permisos**
```xml
<!-- Funcionalidades de cámara -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Notificaciones y alarmas -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.VIBRATE" />

<!-- Autenticación biométrica -->
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<uses-permission android:name="android.permission.USE_FINGERPRINT" />
```

## 🏆 Logros del Proyecto

### **✨ Funcionalidades Únicas Implementadas**
1. **Sistema de gamificación integral** - XP, niveles y logros
2. **Autenticación biométrica** - Huella y reconocimiento facial
3. **Diferenciación automática de perfiles** - Estudiante vs Trabajador
4. **Pomodoro con gamificación** - Integración completa
5. **Dashboard estadístico visual** - Gráficos de productividad
6. **Notificaciones inteligentes** - Para cada funcionalidad
7. **Navegación exhaustiva** - 13+ pantallas interconectadas

### **🎯 Calidad de Código**
- **100% Kotlin** - Código moderno y typesafe
- **Arquitectura MVVM completa** - Separación clara de responsabilidades  
- **0 problemas de integración** - Todos los sistemas conectados
- **Base de datos robusta** - Relaciones y migraciones correctas
- **UI/UX consistente** - Material Design 3 en toda la app

### **� Métricas del Proyecto**
- **7 Entidades** de base de datos
- **9 ViewModels** completamente integrados
- **13+ Pantallas** con navegación
- **15+ Logros** desbloqueables
- **3 Tipos** de notificaciones (Tareas, Pomodoro, Gamificación)
- **2 Perfiles** de usuario (Estudiante/Trabajador)

## 💡 Características Destacadas

**🔥 Innovaciones Técnicas:**
- Gamificación completamente integrada en todas las funcionalidades
- Autenticación biométrica con fallback a contraseña
- Sistema de notificaciones contextual e inteligente
- Dashboard de productividad con métricas visuales
- Arquitectura preparada para escalabilidad horizontal

**🎨 Experiencia de Usuario:**
- Interfaz moderna con Material Design 3
- Navegación intuitiva y fluida
- Personalización de colores y preferencias
- Feedback visual inmediato en todas las acciones
- Onboarding y guías contextuales