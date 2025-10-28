# 🧪 Guía de Pruebas - Autenticación Biométrica

## ✅ Estado: App instalada en emulador
**Dispositivo:** Medium Phone API 36.0 (AVD)  
**Build:** DEBUG ✅ SUCCESSFUL

---

## 📱 Pasos para Probar la Funcionalidad

### 🔹 **PRUEBA 1: Primera Ejecución (Sin Biometría)**

1. **Abre la app** desde el emulador
2. Deberías ver el **SplashScreen** (logo de FocusUp)
3. Luego te llevará al **LoginScreen** (primera vez)
4. **Regístrate** con:
   - Nombre: "Usuario Test"
   - Email: test@focusup.com
   - Contraseña: test123
   - Tipo: Estudiante/Trabajador
5. Accederás al **HomeScreen** ✅

---

### 🔹 **PRUEBA 2: Activar Biometría**

1. En el **HomeScreen**, toca el ícono de **Persona** (arriba derecha)
2. Entrarás al **ProfileScreen**
3. Baja hasta la sección **"Seguridad"** (nueva)
4. Verás el switch **"Autenticación Biométrica"**

#### ⚠️ **Limitación del Emulador:**
El emulador **NO TIENE hardware biométrico real**, por lo que el switch mostrará:
- ❌ **"Este dispositivo no tiene hardware biométrico"**
- ❌ Switch **deshabilitado** (gris)

**Resultado esperado:** El switch NO se puede activar en el emulador.

---

### 🔹 **PRUEBA 3: Simular Biometría en Emulador (Avanzado)**

#### Opción A: Configurar Huella en Emulador
1. Abre **Settings** del emulador (⚙️)
2. Ve a **Security → Fingerprint**
3. Registra una huella simulada
4. Vuelve a FocusUp → Perfil → Seguridad
5. El switch debería estar **habilitado** ahora
6. Activa el switch
7. Aparecerá el diálogo de autenticación biométrica
8. En el emulador, usa **Extended Controls (...)** → **Fingerprint** → Toca "Touch the sensor"
9. Si es exitoso: ✅ **Biometría activada**

#### Opción B: Usar Dispositivo Físico (Recomendado)
Para pruebas reales, necesitas:
- 📱 **Dispositivo físico** con huella digital o Face ID
- 🔌 Conectar por USB con depuración activada
- 🔄 Reinstalar: `.\gradlew installDebug`

---

### 🔹 **PRUEBA 4: Flujo Completo con Biometría Activada**

**Prerrequisito:** Biometría activada desde PRUEBA 3

1. **Cierra la app** completamente (swipe desde recientes)
2. **Abre FocusUp** de nuevo
3. Verás el **SplashScreen**
4. Luego aparecerá **BiometricLockScreen** (nuevo) 🔒
   - Icono grande de huella digital
   - Mensaje: "Hola, [Tu nombre]"
   - "Usa tu huella digital o Face ID para desbloquear"
5. **Automáticamente** se abrirá el diálogo de biometría
6. En emulador: Usa **Extended Controls** → Touch sensor
7. Si es exitoso: ✅ Accedes al **HomeScreen**
8. Si fallas: ❌ Puedes reintentar o tocar **"Usar contraseña"**

---

### 🔹 **PRUEBA 5: Desactivar Biometría**

1. En **HomeScreen** → Perfil (ícono persona)
2. Baja a **Seguridad**
3. **Desactiva** el switch (no requiere verificación)
4. ✅ Biometría desactivada
5. Cierra y abre la app
6. **NO** debería aparecer BiometricLockScreen
7. Va directo a **HomeScreen**

---

### 🔹 **PRUEBA 6: Opciones Secundarias**

#### A. Botón "Usar contraseña"
1. Activa biometría
2. Cierra y abre la app
3. En **BiometricLockScreen**, toca **"Usar contraseña en su lugar"**
4. Debería cerrar sesión y volver a **LoginScreen**

#### B. Reintentar autenticación
1. Abre la app con biometría activa
2. Falla la autenticación intencionalmente
3. Debería aparecer mensaje de error en rojo
4. Toca **"Intentar de nuevo"**
5. Solicita autenticación nuevamente

---

## 🎯 Checklist de Funcionalidades

### UI/Visual
- [ ] SplashScreen se muestra correctamente
- [ ] Sección "Seguridad" aparece en ProfileScreen
- [ ] Switch de biometría con ícono de huella (🔑)
- [ ] BiometricLockScreen tiene diseño elegante
- [ ] Mensajes de estado claros (disponible/no disponible)
- [ ] Diálogos de error con ícono de advertencia

### Funcional
- [ ] Detecta hardware biométrico correctamente
- [ ] Switch solo habilitado si hay hardware
- [ ] Requiere verificación para activar
- [ ] No requiere verificación para desactivar
- [ ] Configuración persiste después de cerrar app
- [ ] BiometricLockScreen aparece solo si está activada
- [ ] Autenticación exitosa permite acceso
- [ ] Autenticación fallida permite reintentar
- [ ] Botón "Usar contraseña" cierra sesión

### Navegación
- [ ] Splash → BiometricLock (si activada) → Home
- [ ] Splash → Login (si no hay sesión)
- [ ] BiometricLock → Home (autenticado)
- [ ] BiometricLock → Login (cancelado)
- [ ] Profile → Configuración de biometría

---

## 🐛 Problemas Conocidos

### ⚠️ Limitaciones del Emulador
1. **Hardware simulado:** El emulador NO tiene biometría real
2. **Extended Controls:** Requiere configuración manual
3. **Face ID:** No soportado en emulador Android estándar

### ✅ Solución
Para pruebas completas, usa **dispositivo físico** con:
- Android 6.0+ (API 23+)
- Huella digital o Face ID configurados
- Depuración USB activada

---

## 📊 Resultados de Compilación

```
✅ BUILD SUCCESSFUL
✅ APK instalado en: Medium_Phone_API_36.0(AVD)
✅ Sin errores de código
✅ Todas las pantallas compiladas
✅ Navegación configurada
```

---

## 🎬 Próximos Pasos

### Si Todo Funciona:
1. ✅ Marcar como completado en PROGRESS.md
2. 🎉 Celebrar la primera funcionalidad avanzada
3. 🚀 Elegir siguiente feature (Dashboard, Gamificación, etc.)

### Si Hay Problemas:
1. 📝 Reportar el error específico
2. 🔍 Revisar logs: `.\gradlew installDebug --info`
3. 🛠️ Corregir y recompilar

---

## 📱 Comandos Útiles

```powershell
# Reinstalar app
.\gradlew installDebug

# Ver logs en tiempo real
.\gradlew installDebug; adb logcat | Select-String "FocusUp"

# Limpiar y compilar desde cero
.\gradlew clean assembleDebug installDebug

# Desinstalar app
.\gradlew uninstallDebug
```

---

## 🎨 Capturas de Pantalla Esperadas

### 1. ProfileScreen - Sección Seguridad
```
┌─────────────────────────────┐
│  🔐 Seguridad               │
│                             │
│  👆 Autenticación           │
│     Biométrica        [ ○ ] │
│     Disponible / No         │
│     disponible              │
└─────────────────────────────┘
```

### 2. BiometricLockScreen
```
┌─────────────────────────────┐
│                             │
│         👆 (grande)         │
│                             │
│        FocusUp              │
│                             │
│      Hola, Usuario          │
│                             │
│  Usa tu huella digital o    │
│  Face ID para desbloquear   │
│                             │
│  [  Intentar de nuevo  ]    │
│                             │
│  Usar contraseña en su      │
│  lugar                      │
└─────────────────────────────┘
```

---

**Estado:** ✅ Listo para pruebas  
**Compilación:** BUILD SUCCESSFUL  
**Instalación:** Completada  
**Siguiente paso:** Abrir FocusUp en el emulador y probar el flujo completo 🚀
