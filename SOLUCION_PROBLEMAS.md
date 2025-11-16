# Solución de Problemas - Aplicación no se ejecuta

Si la aplicación no se muestra corriendo en el emulador después de reorganizar la estructura MVVM, sigue estos pasos:

## 1. Sincronizar el Proyecto con Gradle

1. En Android Studio, ve a **File → Sync Project with Gradle Files**
2. O haz clic en el ícono de sincronización en la barra de herramientas
3. Espera a que termine la sincronización

## 2. Limpiar y Reconstruir el Proyecto

1. Ve a **Build → Clean Project**
2. Espera a que termine
3. Luego ve a **Build → Rebuild Project**
4. Espera a que termine la compilación

## 4. Invalidar Caché y Reiniciar

1. Ve a **File → Invalidate Caches...**
2. Selecciona **Invalidate and Restart**
3. Espera a que Android Studio se reinicie

## 5. Verificar Errores de Compilación

1. Abre la pestaña **Build** en la parte inferior de Android Studio
2. Busca errores en rojo
3. Si hay errores, corrígelos antes de ejecutar

## 6. Verificar Logcat

1. Abre la pestaña **Logcat** en la parte inferior
2. Filtra por "AndroidRuntime" o "FATAL"
3. Busca errores que indiquen por qué la app se cierra

## 7. Verificar que el Emulador esté Corriendo

1. Asegúrate de que el emulador esté completamente iniciado
2. Verifica que aparezca en la lista de dispositivos disponibles
3. Si no aparece, reinicia el emulador

## 8. Reinstalar la Aplicación

1. Desinstala la aplicación del emulador si ya está instalada
2. Ejecuta la aplicación nuevamente desde Android Studio

## Posibles Errores Comunes

### Error: "Cannot resolve symbol 'ViewModelProvider'"
**Solución**: Sincroniza el proyecto con Gradle Files

### Error: "ClassNotFoundException: MainActivity"
**Solución**: Verifica que el AndroidManifest.xml tenga las rutas correctas:
- `.view.MainActivity` en lugar de `.MainActivity`

### Error: "LocationTracker no inicializado"
**Solución**: Verifica que MyApp esté configurado correctamente en el AndroidManifest.xml

### La app se cierra inmediatamente
**Solución**: 
1. Revisa Logcat para ver el error exacto
2. Verifica que todas las dependencias estén correctamente agregadas
3. Asegúrate de que Firebase esté inicializado correctamente

## Verificar Estructura de Carpetas

Asegúrate de que la estructura sea:
```
app/src/main/java/com/example/proyectoandroid/
├── view/
│   ├── MainActivity.java
│   ├── InformacionActivity.java
│   └── ...
├── viewmodel/
│   ├── LoginViewModel.java
│   └── InformacionViewModel.java
├── repository/
│   ├── AuthRepository.java
│   └── LocationRepository.java
├── model/
│   └── ...
├── util/
│   ├── LocationTracker.java
│   └── RutUtils.java
└── MyApp.java
```

## Si Nada Funciona

1. Cierra Android Studio completamente
2. Elimina las carpetas `.gradle` y `build` del proyecto (si existen)
3. Abre Android Studio nuevamente
4. Sincroniza el proyecto
5. Limpia y reconstruye

