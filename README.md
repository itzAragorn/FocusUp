# 🎯 FocusUp - Aplicación de Productividad y Gestión de Tareas

- **Pagina de Inicio**
![WhatsApp Image 2025-10-29 at 8 17 25 PM](https://github.com/user-attachments/assets/d04758f6-2dbe-4f76-a0bf-eabc0de71ae7)

- **Calendario**
![WhatsApp Image 2025-10-29 at 8 17 25 PM (1)](https://github.com/user-attachments/assets/631ecf34-39be-4c7f-a521-59625adb4e9c)

- **Horario**
![WhatsApp Image 2025-10-29 at 8 17 24 PM](https://github.com/user-attachments/assets/5cf12222-1040-474b-a624-633ecb8e764e)

- **Temporizador Pomodoro**
![WhatsApp Image 2025-10-29 at 8 17 24 PM (1)](https://github.com/user-attachments/assets/865689d3-fffc-41aa-885a-c1a9b322637f)

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

## 📱 **Instalación y Uso**

### 🔧 **Requisitos del Sistema**
- **Android 7.0** (API 24) o superior
- **64 MB** de espacio libre de almacenamiento
- **Permisos**: Cámara, Notificaciones, Biometría (opcional)

### 📲 **Opción 1: Instalar APK Precompilado**

#### **Descargar el APK:**
El APK está disponible en la carpeta raíz del proyecto: `FocusUp-v1.0-debug.apk`

#### **Instalación en Android:**
1. **Habilita "Fuentes desconocidas"** en tu dispositivo:
   - Configuración → Seguridad → Fuentes desconocidas ✅
   - O Configuración → Aplicaciones → Acceso especial → Instalar apps desconocidas

2. **Transfiere el APK** a tu celular:
   - Por cable USB y copia directa
   - Por WhatsApp/Email/Google Drive
   - Por ADB: `adb install FocusUp-v1.0-debug.apk`

3. **Instala la aplicación**:
   - Navega al archivo APK en tu celular
   - Toca el archivo para iniciar la instalación
   - Acepta los permisos solicitados

#### **Permisos que solicitará:**
- 📷 **Cámara** - Para adjuntar fotos a las tareas
- 🔔 **Notificaciones** - Para recordatorios de tareas y Pomodoro
- 📁 **Almacenamiento** - Para guardar fotos de tareas
- 🔒 **Biometría** - Para desbloqueo con huella/reconocimiento facial (opcional)

### 🛠️ **Opción 2: Compilar desde Código Fuente**

#### **Requisitos de Desarrollo:**
- **Android Studio** 2024.1.1 o superior
- **JDK 17** o superior
- **Android SDK** con API 34
- **Git** para clonar el repositorio

#### **Pasos de Compilación:**
1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/itzAragorn/FocusUp.git
   cd FocusUp
   git checkout dev-osses
   ```

2. **Abrir en Android Studio:**
   - File → Open → Seleccionar carpeta FocusUp
   - Esperar sincronización de Gradle

3. **Compilar APK Debug:**
   ```bash
   ./gradlew clean assembleDebug
   ```
   **APK generado en:** `app/build/outputs/apk/debug/app-debug.apk`

4. **Compilar APK Release (opcional):**
   ```bash
   ./gradlew clean assembleRelease
   ```

#### **Ejecutar en Emulador:**
```bash
./gradlew installDebug
```

#### **Instalar directamente en dispositivo conectado:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 🚀 **Primeros Pasos en la App**

#### **1. Registro Inicial:**
- Abre FocusUp
- Crea tu cuenta con email y contraseña
- Selecciona tu perfil: **Estudiante** o **Trabajador**

#### **2. Configuración Biométrica (Opcional):**
- Ve a Perfil → Configuración
- Activa "Autenticación Biométrica"
- Configura huella dactilar o reconocimiento facial

#### **3. Crear tu Primer Horario:**
- Ve a la sección "Horarios"
- Toca "+" para agregar un bloque
- Completa: Nombre, horario, color
- **Estudiantes**: Agrega profesor y sala

#### **4. Agregar Tareas:**
- Ve al "Calendario"
- Selecciona una fecha
- Toca "+" para crear tarea
- Agrega descripción, hora, prioridad
- Opcional: Adjunta una foto

#### **5. Usar Técnica Pomodoro:**
- Ve a la sección "Pomodoro"
- Selecciona duración (25/45/60 minutos)
- Inicia tu sesión de enfoque
- Disfruta los descansos automáticos

#### **6. Ver tu Progreso:**
- **Dashboard**: Métricas de productividad
- **Logros**: Ve tu progreso de gamificación
- **Estadísticas**: Gráficos de rendimiento

### 🎮 **Sistema de Gamificación**
- **Gana XP** completando tareas y sesiones Pomodoro
- **Sube de nivel** automáticamente (10 niveles disponibles)
- **Desbloquea logros** por hitos de productividad
- **Recibe notificaciones** de recompensas

### 📊 **Funcionalidades Principales**
- ✅ **Gestión de tareas** con fotos y recordatorios
- ✅ **Horarios semanales** diferenciados por perfil
- ✅ **Técnica Pomodoro** con gamificación integrada
- ✅ **Dashboard estadístico** con métricas visuales
- ✅ **Sistema de logros** motivacional
- ✅ **Autenticación biométrica** segura

## 🎯 **Próximos Pasos Recomendados**

### Para desarrolladores que quieren contribuir:

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

## 🔧 **Solución de Problemas**

### **Problemas de Instalación**

#### **"App no instalada" o "Parse Error":**
- ✅ Verifica que tu Android sea **7.0 o superior**
- ✅ Asegúrate de tener **espacio suficiente** (64+ MB)
- ✅ Descarga nuevamente el APK si está corrupto
- ✅ Habilita **"Fuentes desconocidas"** en configuración

#### **"Permisos denegados":**
- ✅ Ve a Configuración → Aplicaciones → FocusUp → Permisos
- ✅ Activa manualmente: **Cámara**, **Notificaciones**, **Almacenamiento**
- ✅ Para biometría: Configuración → Seguridad → Huella dactilar

### **Problemas de Funcionamiento**

#### **Las notificaciones no aparecen:**
- ✅ Configuración → Aplicaciones → FocusUp → Notificaciones → **Activar**
- ✅ Configuración → Batería → Optimización → **Excluir FocusUp**
- ✅ Verifica que tengas **permisos de notificaciones**

#### **La cámara no funciona:**
- ✅ Configuración → Aplicaciones → FocusUp → Permisos → **Cámara: Permitir**
- ✅ Cierra otras apps que usen la cámara
- ✅ Reinicia la aplicación

#### **Autenticación biométrica falla:**
- ✅ Verifica que tu dispositivo **soporte biometría**
- ✅ Configura huella/reconocimiento facial en **Configuración del sistema**
- ✅ Si falla, usa **contraseña como alternativa**

#### **Los datos se pierden:**
- ✅ La app usa **base de datos local** (Room)
- ✅ No desinstales la app para conservar datos
- ✅ Los datos se guardan automáticamente

### **Problemas de Compilación (Desarrolladores)**

#### **"Gradle sync failed":**
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

#### **"KSP compilation error":**
- ✅ Verifica que uses **JDK 17+**
- ✅ Android Studio **2024.1.1+**
- ✅ Sincroniza proyecto: Tools → Sync Project with Gradle Files

#### **"Build failed" en APK:**
```bash
./gradlew clean
./gradlew assembleDebug --stacktrace
```

### **📞 Soporte**
- **Repositorio**: https://github.com/itzAragorn/FocusUp
- **Issues**: Reporta problemas en GitHub Issues
- **Documentación**: Revisa PROGRESS.md para detalles técnicos

### **📊 Información del Sistema**
- **Versión actual**: 1.0 (Debug)
- **API mínima**: Android 24 (Android 7.0)
- **Tamaño**: ~22 MB
- **Arquitectura**: MVVM con Room Database
- **Lenguaje**: 100% Kotlin con Jetpack Compose
