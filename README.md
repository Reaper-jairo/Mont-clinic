# Mont Clinic

## 2. Problemática
Actualmente, los pacientes que necesitan retirar insumos médicos en la clínica o en la farmacia autorizada deben esperar en largas filas, lo que genera pérdida de tiempo y desorden en la atención del cliente. Esto dificulta la organización del centro de salud y afecta la experiencia de los usuarios.

### Aporte
La aplicación permite a los clientes agendar con anticipación una fecha y hora específica para el retiro de sus insumos, optimizando su tiempo, evitando aglomeraciones y facilitando una mejor organización en el centro de distribución.

---

## 3. Pantallas
- **Pantalla básica**: el cliente inicia sesión con su RUT para verificar si sus datos son correctos.  
- **Login**: permite ver la fecha del cliente y agendar el mismo día, además de revisar si hay insumos médicos disponibles.  
- **Perfil y configuración**: el cliente puede gestionar y actualizar sus datos.  

---

## 4. Navegación entre pantallas
1. **Login → Menú principal**  
2. **Menú principal → Agendamiento**  
3. **Agendamiento → Confirmación** (verificar que los datos estén correctos)  
4. **Menú principal → Historial** (carga de reservas con ID de usuario)  

---

## 5. Componentes de Android previstos
- **Activities**: para las pantallas principales.  
- **Intents**: para navegación y traspaso de datos entre actividades (ej. fecha/hora).  
- **Service (opcional)**: para notificaciones de recordatorio de la hora agendada.  
- **BroadcastReceiver (opcional)**: para recibir alarmas del sistema y disparar notificaciones.  

---

## 6. Datos necesarios
- **Cliente**: nombre, RUT, correo, número de teléfono.  
- **Reservas**: fecha y hora de la cita.  

---

## 7. Consideraciones técnicas
- Validar correctamente la disponibilidad de horarios para evitar sobrecupo.  
- Asegurar la persistencia de datos aunque el usuario cierre la app (uso de base de datos local).  
- Gestión de notificaciones (recordatorios automáticos).  

---

## 8. Plan de trabajo
- **Semana 1**: Diseño de interfaces básicas (pantallas principales en Android Studio).  
- **Semana 2**: Implementación de navegación con Intents y traspaso de datos.  
- **Semana 3**: Integración de almacenamiento local de citas y simulación de notificaciones.  
