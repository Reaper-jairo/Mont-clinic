# Guía de Configuración de Firebase

Esta guía te explica qué necesitas crear en Firebase Firestore para que la aplicación funcione correctamente.

## Estructura de Colecciones en Firestore

### 1. Colección `rut_index`

**Propósito**: Índice para buscar el email de un paciente por su RUT.

**Estructura del documento**:
```
Document ID: {RUT_NORMALIZADO} (ej: "12345678K")
Campos:
  - email: string (ej: "juan.perez@example.com")
```

**Ejemplo**:
```json
{
  "email": "juan.perez@example.com"
}
```

**Nota**: El RUT debe estar normalizado (sin puntos, sin guiones, en mayúsculas).

---

### 2. Colección `pacientes`

**Propósito**: Almacena la información completa de cada paciente.

**Estructura del documento**:
```
Document ID: {RUT_NORMALIZADO} (ej: "12345678K")
Campos:
  - email: string (obligatorio)
  - nombre: string (obligatorio)
  - rut: string (obligatorio)
  - telefono: string (opcional)
  - direccion: string (opcional)
```

**Ejemplo**:
```json
{
  "email": "juan.perez@example.com",
  "nombre": "Juan Pérez González",
  "rut": "12345678K",
  "telefono": "+56912345678",
  "direccion": "Calle Principal 123, Comuna"
}
```

---

### 3. Colección `atenciones`

**Propósito**: Registra las atenciones médicas realizadas a los pacientes.

**Estructura del documento**:
```
Document ID: (auto-generado por Firestore)
Campos:
  - emailPaciente: string (obligatorio)
  - rutPaciente: string (obligatorio)
  - fecha: timestamp (obligatorio)
  - motivo: string (obligatorio)
  - medico: string (obligatorio)
  - diagnostico: string (opcional)
  - observaciones: string (opcional)
```

**Ejemplo**:
```json
{
  "emailPaciente": "juan.perez@example.com",
  "rutPaciente": "12345678K",
  "fecha": Timestamp(2024, 3, 15, 10, 30, 0),
  "motivo": "Control de presión arterial",
  "medico": "Dr. García",
  "diagnostico": "Presión arterial normal",
  "observaciones": "Continuar con medicación actual"
}
```

**Índices necesarios**:
- Crear un índice compuesto en Firestore Console:
  - Campo: `emailPaciente` (Ascending)
  - Campo: `fecha` (Descending)

---

### 4. Colección `citas`

**Propósito**: Almacena las citas médicas programadas.

**Estructura del documento**:
```
Document ID: (auto-generado por Firestore)
Campos:
  - emailPaciente: string (obligatorio)
  - rutPaciente: string (obligatorio)
  - fecha: timestamp (obligatorio)
  - hora: string (obligatorio, formato: "HH:mm")
  - motivo: string (obligatorio)
  - medico: string (obligatorio)
  - tipo: string (opcional, valores: "consulta", "control", "examen")
  - estado: string (obligatorio, valores: "pendiente", "confirmada", "cancelada")
```

**Ejemplo**:
```json
{
  "emailPaciente": "juan.perez@example.com",
  "rutPaciente": "12345678K",
  "fecha": Timestamp(2024, 3, 25, 0, 0, 0),
  "hora": "10:30",
  "motivo": "Consulta general",
  "medico": "Dr. García",
  "tipo": "consulta",
  "estado": "pendiente"
}
```

**Índices necesarios**:
- Crear un índice compuesto en Firestore Console:
  - Campo: `emailPaciente` (Ascending)
  - Campo: `fecha` (Ascending)

---

### 5. Colección `medicamentos`

**Propósito**: Almacena los medicamentos recetados a los pacientes.

**Estructura del documento**:
```
Document ID: (auto-generado por Firestore)
Campos:
  - emailPaciente: string (obligatorio)
  - rutPaciente: string (obligatorio)
  - nombre: string (obligatorio, ej: "Paracetamol 500mg")
  - dosis: string (obligatorio, ej: "1 tableta")
  - frecuencia: string (obligatorio, ej: "Cada 8 horas", "2 veces al día")
  - fechaInicio: timestamp (obligatorio)
  - fechaFin: timestamp (obligatorio)
  - medico: string (obligatorio)
  - observaciones: string (opcional)
```

**Ejemplo**:
```json
{
  "emailPaciente": "juan.perez@example.com",
  "rutPaciente": "12345678K",
  "nombre": "Paracetamol 500mg",
  "dosis": "1 tableta",
  "frecuencia": "Cada 8 horas",
  "fechaInicio": Timestamp(2024, 3, 15, 0, 0, 0),
  "fechaFin": Timestamp(2024, 3, 25, 0, 0, 0),
  "medico": "Dr. García",
  "observaciones": "Tomar con alimentos"
}
```

**Índices necesarios**:
- Crear un índice compuesto en Firestore Console:
  - Campo: `emailPaciente` (Ascending)
  - Campo: `fechaInicio` (Descending)
- Query scope: Collection

---

## Pasos para Configurar Firebase

### Paso 1: Crear las Colecciones

1. Ve a la consola de Firebase: https://console.firebase.google.com
2. Selecciona tu proyecto
3. Ve a **Firestore Database**
4. Crea las 4 colecciones mencionadas arriba

### Paso 2: Crear los Índices

1. En Firestore Console, ve a la pestaña **Índices**
2. Crea los siguientes índices compuestos:

**Para `atenciones`**:
- Colección: `atenciones`
- Campos:
  - `emailPaciente` (Ascending)
  - `fecha` (Descending)
- Query scope: Collection

**Para `citas`**:
- Colección: `citas`
- Campos:
  - `emailPaciente` (Ascending)
  - `fecha` (Ascending)
- Query scope: Collection

**Para `medicamentos`**:
- Colección: `medicamentos`
- Campos:
  - `emailPaciente` (Ascending)
  - `fechaInicio` (Descending)
- Query scope: Collection

### Paso 3: Configurar Reglas de Seguridad (Opcional pero Recomendado)

En Firestore Console, ve a **Reglas** y configura algo como:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Permitir lectura/escritura solo a usuarios autenticados
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
    
    // O reglas más específicas:
    match /pacientes/{rut} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    match /atenciones/{atencionId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    match /citas/{citaId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    match /rut_index/{rut} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
    
    match /medicamentos/{medicamentoId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

### Paso 4: Habilitar Authentication

1. En Firebase Console, ve a **Authentication**
2. Habilita el método **Email/Password**
3. Los usuarios se crearán automáticamente cuando registres pacientes desde la app

---

## Flujo de Datos

### Al Registrar un Paciente:

1. Se crea usuario en **Authentication** (email + password)
2. Se crea documento en `rut_index/{RUT}` con el email
3. Se crea documento en `pacientes/{RUT}` con todos los datos

### Al Registrar una Atención:

1. Se busca el email del paciente en `rut_index` usando el RUT
2. Se crea documento en `atenciones` con todos los datos

### Al Agendar una Cita:

1. Se busca el email del paciente en `rut_index` usando el RUT
2. Se crea documento en `citas` con todos los datos

### Actualización en Tiempo Real:

La aplicación usa **listeners en tiempo real** de Firestore para:
- **Atenciones**: Se actualizan automáticamente cuando hay cambios
- **Citas**: Se actualizan automáticamente cuando hay cambios
- **Medicamentos**: Se actualizan automáticamente cuando hay cambios

Esto significa que si un médico agrega un medicamento, atención o cita desde otra aplicación o desde la consola de Firebase, la app del paciente se actualizará automáticamente sin necesidad de recargar.

---

## Datos de Prueba

Puedes crear manualmente estos documentos para probar:

### 1. Crear paciente de prueba:

**En `rut_index`**:
- Document ID: `12345678K`
- `email`: `test@example.com`

**En `pacientes`**:
- Document ID: `12345678K`
- `email`: `test@example.com`
- `nombre`: `Juan Pérez`
- `rut`: `12345678K`
- `telefono`: `+56912345678`
- `direccion`: `Calle Test 123`

**En Authentication**:
- Email: `test@example.com`
- Password: `123456`

### 2. Crear atención de prueba:

**En `atenciones`** (nuevo documento):
- `emailPaciente`: `test@example.com`
- `rutPaciente`: `12345678K`
- `fecha`: Timestamp (fecha actual)
- `motivo`: `Control de presión`
- `medico`: `Dr. García`
- `diagnostico`: `Normal`
- `observaciones`: `Sin observaciones`

### 3. Crear cita de prueba:

**En `citas`** (nuevo documento):
- `emailPaciente`: `test@example.com`
- `rutPaciente`: `12345678K`
- `fecha`: Timestamp (fecha futura)
- `hora`: `10:30`
- `motivo`: `Consulta general`
- `medico`: `Dr. García`
- `tipo`: `consulta`
- `estado`: `pendiente`

---

## Notas Importantes

1. **RUT Normalizado**: Siempre usa RUT sin puntos, sin guiones, en mayúsculas (ej: `12345678K`)
2. **Timestamps**: Firestore usa Timestamp, no Date. La app maneja la conversión automáticamente
3. **Índices**: Los índices compuestos son necesarios para las consultas ordenadas
4. **Seguridad**: Configura las reglas de seguridad según tus necesidades

---

## Solución de Problemas

### Error: "Missing or insufficient permissions"
- Verifica las reglas de seguridad de Firestore
- Asegúrate de que el usuario esté autenticado

### Error: "The query requires an index"
- Ve a Firestore Console → Índices
- Crea el índice que Firebase te sugiere (hay un enlace directo en el error)

### No aparecen datos en la app
- Verifica que los documentos tengan el campo `emailPaciente` correcto
- Verifica que el usuario esté autenticado con el mismo email

