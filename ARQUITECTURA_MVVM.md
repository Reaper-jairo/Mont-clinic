# Arquitectura MVVM Implementada

Este documento describe la arquitectura MVVM (Model-View-ViewModel) implementada en el proyecto Mont-clinic.

## Estructura de Carpetas

```
app/src/main/java/com/example/proyectoandroid/
├── model/              # Modelos de datos
│   ├── User.java
│   ├── RutValidation.java
│   └── LocationState.java
├── repository/         # Repositorios (acceso a datos)
│   ├── AuthRepository.java
│   └── LocationRepository.java
├── viewmodel/          # ViewModels (lógica de presentación)
│   ├── LoginViewModel.java
│   └── InformacionViewModel.java
├── view/               # Vistas (UI - Activities)
│   ├── MainActivity.java
│   ├── InformacionActivity.java
│   ├── AgendarHoraActivity.java
│   ├── SolicitarHoraActivity.java
│   └── MedicamentosActivity.java
├── util/               # Utilidades
│   ├── RutUtils.java
│   └── LocationTracker.java
└── MyApp.java          # Clase Application principal
```

## Componentes de la Arquitectura

### 1. Model (Modelo)
Contiene las clases de datos y lógica de negocio pura:
- **User**: Representa un usuario del sistema
- **RutValidation**: Resultado de validación de RUT
- **LocationState**: Estado de la ubicación GPS

### 2. Repository (Repositorio)
Maneja el acceso a datos y fuentes externas:
- **AuthRepository**: 
  - Autenticación con Firebase Auth
  - Búsqueda de email por RUT en Firestore
  - Gestión de sesión de usuario
  
- **LocationRepository**:
  - Obtención de ubicación GPS
  - Gestión de solicitudes de ubicación

### 3. ViewModel
Contiene la lógica de presentación y se comunica con los repositorios:
- **LoginViewModel**:
  - Validación de RUT y contraseña
  - Gestión del flujo de login
  - Exposición de estados mediante LiveData
  
- **InformacionViewModel**:
  - Gestión de ubicación GPS
  - Gestión de sesión de usuario
  - Exposición de estados mediante LiveData

### 4. View (Vista)
Las Activities que manejan la UI:
- **MainActivity**: Pantalla de login
- **InformacionActivity**: Pantalla principal con navegación

## Flujo de Datos

```
View (Activity) 
    ↓ (observa)
ViewModel (LiveData)
    ↓ (llama)
Repository
    ↓ (accede)
Data Source (Firebase, GPS, etc.)
```

## Beneficios de MVVM

1. **Separación de responsabilidades**: La lógica de negocio está separada de la UI
2. **Testabilidad**: Los ViewModels pueden probarse independientemente
3. **Mantenibilidad**: Código más organizado y fácil de mantener
4. **Reutilización**: Los ViewModels pueden reutilizarse en diferentes vistas
5. **Ciclo de vida**: Los ViewModels sobreviven a los cambios de configuración

## Dependencias Agregadas

```gradle
// MVVM Architecture Components
implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")
implementation("androidx.lifecycle:lifecycle-runtime:2.8.7")
implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.7")
```

## Uso de LiveData

Los ViewModels exponen datos mediante `LiveData`, que permite:
- Observar cambios en los datos
- Actualización automática de la UI
- Gestión del ciclo de vida (no hay memory leaks)

## Ejemplo de Uso

### En una Activity:

```java
// Inicializar ViewModel
viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

// Observar cambios
viewModel.getLoginSuccess().observe(this, success -> {
    if (success != null && success) {
        // Navegar a siguiente pantalla
    }
});

// Llamar a métodos del ViewModel
viewModel.iniciarSesion(rut, password);
```

## Notas Importantes

- Los ViewModels NO deben tener referencias a Views (Activities, Fragments)
- Los Repositorios usan callbacks para evitar memory leaks con LiveData
- Los ViewModels sobreviven a los cambios de configuración (rotación de pantalla)
- Se usa `observeForever` con cuidado, removiendo los observers después de usarlos

