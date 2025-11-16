package com.example.proyectoandroid.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.Atencion;
import com.example.proyectoandroid.model.Cita;
import com.example.proyectoandroid.model.Paciente;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio para manejar datos del paciente desde Firebase
 */
public class PacienteRepository {
    private static PacienteRepository instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private ListenerRegistration atencionesListener;
    private ListenerRegistration citasListener;

    private PacienteRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized PacienteRepository getInstance() {
        if (instance == null) {
            instance = new PacienteRepository();
        }
        return instance;
    }

    /**
     * Registra un nuevo paciente en el sistema
     */
    public void registrarPaciente(String rutNormalizado, String email, String password, 
                                   String nombre, String telefono, String direccion,
                                   RegistroCallback callback) {
        // Asegurar que el RUT esté en mayúsculas para consistencia
        String rutFinal = rutNormalizado != null ? rutNormalizado.toUpperCase() : "";
        
        // Crear usuario en Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    // Crear /rut_index/{RUT}
                    Map<String, Object> rutIndex = new HashMap<>();
                    rutIndex.put("email", email);

                    // Crear /pacientes/{RUT}
                    Map<String, Object> paciente = new HashMap<>();
                    paciente.put("email", email);
                    paciente.put("nombre", nombre);
                    paciente.put("rut", rutFinal);
                    paciente.put("telefono", telefono != null ? telefono : "");
                    paciente.put("direccion", direccion != null ? direccion : "");

                    // Ejecutar ambas escrituras en paralelo
                    Tasks.whenAllComplete(
                            db.collection("rut_index").document(rutFinal).set(rutIndex),
                            db.collection("pacientes").document(rutFinal).set(paciente)
                    ).addOnSuccessListener(list -> {
                        callback.onSuccess();
                    }).addOnFailureListener(e -> {
                        callback.onError("Error guardando datos: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    callback.onError("Error creando usuario: " + e.getMessage());
                });
    }

    /**
     * Obtiene los datos del paciente actual
     */
    public void obtenerDatosPaciente(PacienteCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            callback.onResult(null);
            return;
        }

        // Buscar paciente por email
        db.collection("pacientes")
                .whereEqualTo("email", user.getEmail())
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        Paciente paciente = new Paciente();
                        paciente.setNombre(doc.getString("nombre"));
                        paciente.setRut(doc.getString("rut"));
                        paciente.setEmail(doc.getString("email"));
                        paciente.setTelefono(doc.getString("telefono"));
                        paciente.setDireccion(doc.getString("direccion"));
                        callback.onResult(paciente);
                    } else {
                        // Si no se encuentra, crear un paciente básico con datos del usuario
                        Paciente paciente = new Paciente();
                        paciente.setNombre(user.getDisplayName() != null ? user.getDisplayName() : "Usuario");
                        paciente.setEmail(user.getEmail());
                        callback.onResult(paciente);
                    }
                })
                .addOnFailureListener(e -> callback.onResult(null));
    }

    /**
     * Obtiene las últimas atenciones del paciente con actualización en tiempo real
     */
    public void obtenerUltimasAtenciones(int limite, AtencionesCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            callback.onResult(new ArrayList<>());
            return;
        }

        // Remover listener anterior si existe
        if (atencionesListener != null) {
            atencionesListener.remove();
        }

        // Crear listener en tiempo real
        atencionesListener = db.collection("atenciones")
                .whereEqualTo("emailPaciente", user.getEmail())
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limite)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        callback.onResult(new ArrayList<>());
                        return;
                    }

                    if (queryDocumentSnapshots == null) {
                        callback.onResult(new ArrayList<>());
                        return;
                    }

                    List<Atencion> atenciones = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Atencion atencion = new Atencion();
                        atencion.setId(doc.getId());
                        atencion.setMotivo(doc.getString("motivo"));
                        atencion.setMedico(doc.getString("medico"));
                        atencion.setDiagnostico(doc.getString("diagnostico"));
                        atencion.setObservaciones(doc.getString("observaciones"));
                        
                        // Convertir timestamp a Date
                        if (doc.getTimestamp("fecha") != null) {
                            atencion.setFecha(doc.getTimestamp("fecha").toDate());
                        }
                        
                        atenciones.add(atencion);
                    }
                    callback.onResult(atenciones);
                });
    }

    /**
     * Obtiene las próximas citas del paciente con actualización en tiempo real
     */
    public void obtenerProximasCitas(int limite, CitasCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            callback.onResult(new ArrayList<>());
            return;
        }

        // Remover listener anterior si existe
        if (citasListener != null) {
            citasListener.remove();
        }

        // Crear listener en tiempo real
        // Nota: Si hay un índice compuesto en Firestore para (emailPaciente, fecha), usar orderBy
        // Si no, obtener todas y filtrar localmente
        citasListener = db.collection("citas")
                .whereEqualTo("emailPaciente", user.getEmail())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Si hay error de índice, intentar sin orderBy
                        android.util.Log.e("PacienteRepository", "Error obteniendo citas: " + e.getMessage());
                        // Intentar obtener sin orderBy
                        db.collection("citas")
                                .whereEqualTo("emailPaciente", user.getEmail())
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    procesarCitas(querySnapshot, limite, callback);
                                })
                                .addOnFailureListener(error -> {
                                    android.util.Log.e("PacienteRepository", "Error obteniendo citas sin orderBy: " + error.getMessage());
                                    callback.onResult(new ArrayList<>());
                                });
                        return;
                    }

                    if (queryDocumentSnapshots == null) {
                        callback.onResult(new ArrayList<>());
                        return;
                    }

                    procesarCitas(queryDocumentSnapshots, limite, callback);
                });
    }

    /**
     * Procesa los documentos de citas y los filtra
     */
    private void procesarCitas(com.google.firebase.firestore.QuerySnapshot queryDocumentSnapshots, int limite, CitasCallback callback) {
        // Crear fecha actual cada vez que se procesa para comparación correcta
        Date ahora = new Date();
        
        android.util.Log.d("PacienteRepository", "Procesando citas. Total documentos: " + queryDocumentSnapshots.size());
        
        List<Cita> citas = new ArrayList<>();
        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
            android.util.Log.d("PacienteRepository", "Procesando cita ID: " + doc.getId() + ", email: " + doc.getString("emailPaciente"));
            // Filtrar solo citas futuras y con estado válido
            Date fechaCita = null;
            if (doc.getTimestamp("fecha") != null) {
                fechaCita = doc.getTimestamp("fecha").toDate();
                // Comparar solo la fecha (sin hora) para no filtrar citas del mismo día
                java.util.Calendar calCita = java.util.Calendar.getInstance();
                calCita.setTime(fechaCita);
                calCita.set(java.util.Calendar.HOUR_OF_DAY, 0);
                calCita.set(java.util.Calendar.MINUTE, 0);
                calCita.set(java.util.Calendar.SECOND, 0);
                calCita.set(java.util.Calendar.MILLISECOND, 0);
                
                java.util.Calendar calAhora = java.util.Calendar.getInstance();
                calAhora.setTime(ahora);
                calAhora.set(java.util.Calendar.HOUR_OF_DAY, 0);
                calAhora.set(java.util.Calendar.MINUTE, 0);
                calAhora.set(java.util.Calendar.SECOND, 0);
                calAhora.set(java.util.Calendar.MILLISECOND, 0);
                
                // Solo filtrar si la fecha es anterior a hoy (no incluir hoy)
                if (calCita.getTime().before(calAhora.getTime())) {
                    continue; // Saltar citas pasadas (anteriores a hoy)
                }
            }

            String estado = doc.getString("estado");
            if (estado == null || (!estado.equals("confirmada") && !estado.equals("pendiente"))) {
                continue; // Saltar citas canceladas o con estado inválido
            }

            Cita cita = new Cita();
            cita.setId(doc.getId());
            cita.setHora(doc.getString("hora"));
            cita.setMotivo(doc.getString("motivo"));
            cita.setMedico(doc.getString("medico"));
            cita.setEstado(estado);
            cita.setTipo(doc.getString("tipo"));
            
            // Convertir timestamp a Date
            if (fechaCita != null) {
                cita.setFecha(fechaCita);
            }
            
            citas.add(cita);
        }
        
        // Ordenar por fecha ascendente
        citas.sort((c1, c2) -> {
            if (c1.getFecha() == null && c2.getFecha() == null) return 0;
            if (c1.getFecha() == null) return 1;
            if (c2.getFecha() == null) return -1;
            return c1.getFecha().compareTo(c2.getFecha());
        });
        
        // Limitar resultados
        if (citas.size() > limite) {
            citas = citas.subList(0, limite);
        }
        
        android.util.Log.d("PacienteRepository", "Citas procesadas: " + citas.size() + " de " + queryDocumentSnapshots.size() + " documentos");
        
        callback.onResult(citas);
    }

    /**
     * Detiene todos los listeners
     */
    public void detenerListeners() {
        if (atencionesListener != null) {
            atencionesListener.remove();
            atencionesListener = null;
        }
        if (citasListener != null) {
            citasListener.remove();
            citasListener = null;
        }
    }

    /**
     * Registra una nueva atención médica
     */
    public void registrarAtencion(String emailPaciente, String rutPaciente, Date fecha,
                                   String motivo, String medico, String diagnostico,
                                   String observaciones, RegistroCallback callback) {
        Map<String, Object> atencion = new HashMap<>();
        atencion.put("emailPaciente", emailPaciente);
        atencion.put("rutPaciente", rutPaciente);
        // Firestore acepta Date directamente, lo convierte automáticamente
        atencion.put("fecha", fecha);
        atencion.put("motivo", motivo);
        atencion.put("medico", medico);
        atencion.put("diagnostico", diagnostico != null ? diagnostico : "");
        atencion.put("observaciones", observaciones != null ? observaciones : "");

        db.collection("atenciones")
                .add(atencion)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Error registrando atención: " + e.getMessage());
                });
    }

    /**
     * Agenda una nueva cita
     */
    public void agendarCita(String emailPaciente, String rutPaciente, Date fecha,
                            String hora, String motivo, String medico, String tipo,
                            RegistroCallback callback) {
        Map<String, Object> cita = new HashMap<>();
        cita.put("emailPaciente", emailPaciente);
        cita.put("rutPaciente", rutPaciente);
        // Firestore acepta Date directamente, lo convierte automáticamente
        cita.put("fecha", fecha);
        cita.put("hora", hora);
        cita.put("motivo", motivo);
        cita.put("medico", medico);
        cita.put("tipo", tipo != null ? tipo : "consulta");
        cita.put("estado", "pendiente");

        db.collection("citas")
                .add(cita)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Error agendando cita: " + e.getMessage());
                });
    }

    /**
     * Obtiene el email de un paciente por su RUT
     */
    public void obtenerEmailPorRut(String rutNormalizado, EmailCallback callback) {
        db.collection("rut_index")
                .document(rutNormalizado)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        callback.onResult(email);
                    } else {
                        // Intentar en minúsculas
                        db.collection("rut_index")
                                .document(rutNormalizado.toLowerCase())
                                .get()
                                .addOnSuccessListener(doc -> {
                                    if (doc.exists()) {
                                        callback.onResult(doc.getString("email"));
                                    } else {
                                        callback.onResult(null);
                                    }
                                })
                                .addOnFailureListener(e -> callback.onResult(null));
                    }
                })
                .addOnFailureListener(e -> callback.onResult(null));
    }

    // Interfaces
    public interface PacienteCallback {
        void onResult(Paciente paciente);
    }

    public interface AtencionesCallback {
        void onResult(List<Atencion> atenciones);
    }

    public interface CitasCallback {
        void onResult(List<Cita> citas);
    }

    public interface RegistroCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface EmailCallback {
        void onResult(String email);
    }
}
