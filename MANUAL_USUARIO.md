# MANUAL DE USUARIO PROYECTO ANDROID

**Integrantes:** FELIPE CACERES, JHON BUSTOS, FRANCISCO ZAMBORANO
**Asignatura:** ANDROID
**Docente:** Giovanni Cáceres Rubio
**Fecha:** 29/11/2025

---

## Índice
1. [Inicio de sesión](#1-inicio-de-sesión)
2. [Pantalla de Menú Principal / Información del Usuario](#2-pantalla-de-menú-principal--información-del-usuario)
3. [Agendar Nueva Cita](#3-agendar-nueva-cita)
4. [Medicamentos](#4-medicamentos)

---

## 1. Inicio de sesión

<img width="427" height="700" alt="image" src="https://github.com/user-attachments/assets/85a5972b-8ba7-4790-ab79-53fa7e033999" />


### ¿Para qué sirve?
Permite que el usuario acceda con su RUT y contraseña al sistema Tunomático.
*Solo afiliados a CESFAM pueden iniciar sesión.

### ¿Cómo usarla?
1. Escribe tu **RUT** sin puntos y sin guion.
2. Ingresa tu **contraseña**.
3. Presiona el botón **“Ingresar”**.
4. Si los datos son correctos, entrarás al menú principal.

### Errores comunes
* **“Credenciales inválidas”**: Revisa el RUT o contraseña.
* **“Sin conexión”**: Verifica tu conexión a internet.

---

## 2. Pantalla de Menú Principal / Información del Usuario

### ¿Qué muestra?
* Nombre del paciente.
* Foto o ícono correspondiente.
* **Accesos rápidos a:**
    * Agenda
    * Solicitar hora
    * Medicamentos
    * Cerrar sesión

![Menú lateral con datos del paciente](ruta/a/imagen-menu-lateral.png)

### Función principal del botón GPS
Permite obtener la **ubicación del paciente** para asociarla a solicitudes o citas. La aplicación pedirá los permisos necesarios la primera vez que se use.

---

## 3. Agendar Nueva Cita

Para agendar una nueva cita en la aplicación Tunomático, tienes que ir al botón lateral de **Solicitar** que está en la parte izquierda de las vistas.

Apretando ese botón, serás dirigido a la siguiente pantalla de formulario:

![Formulario para Agendar Nueva Cita](ruta/a/imagen-agendar-cita.png)

### ¿Qué hace cada campo?

1.  **RUT del Paciente**
    * El usuario ingresa el RUT del paciente **sin puntos y con guion**.
    * *Ejemplos válidos:* 12345678-9, 1234567-K.
2.  **Fecha de la Cita**
    * Seleccione la **fecha** en que desea agendar la atención. Se abrirá un calendario donde puede escoger el día.
3.  **Hora**
    * Seleccione la **hora** de atención. Muestra un selector de hora en formato HH:MM.
4.  **Médico**
    * Campo para seleccionar el **profesional** que atenderá la cita. Puede abrir una lista de profesionales disponibles.
5.  **Tipo de Cita**
    * Indica si la hora será una **Consulta**, **Control**, **Urgencia**, o **Visita domiciliaria** (o las opciones que tenga tu sistema).
6.  **Motivo de la Cita**
    * Campo donde el usuario describe brevemente el motivo de la atención.
    * *Ejemplo:* “Dolor de garganta desde hace 3 días”.

---

## 4. Medicamentos

Esta vista permite que, si el doctor ha dejado recetas médicas por meses, el usuario pueda **reservar la hora para ir a retirar los medicamentos** previamente indicados por el doctor.

![Pantalla de Mis Medicamentos sin registros](ruta/a/imagen-medicamentos.png)

*Si no hay registros, mostrará: "No hay medicamentos registrados."*
