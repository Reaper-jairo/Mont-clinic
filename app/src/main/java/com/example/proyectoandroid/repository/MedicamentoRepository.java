package com.example.proyectoandroid.repository;

import com.example.proyectoandroid.model.Medicamento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Repositorio para manejar medicamentos desde Firebase con actualización en tiempo real
 */
public class MedicamentoRepository {
    private static MedicamentoRepository instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private ListenerRegistration medicamentosListener;

    private MedicamentoRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized MedicamentoRepository getInstance() {
        if (instance == null) {
            instance = new MedicamentoRepository();
        }
        return instance;
    }

    /**
     * Obtiene los medicamentos del paciente con actualización en tiempo real
     */
    public void obtenerMedicamentos(MedicamentosCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            callback.onResult(new ArrayList<>());
            return;
        }

        // Remover listener anterior si existe
        if (medicamentosListener != null) {
            medicamentosListener.remove();
        }

        // Crear listener en tiempo real
        medicamentosListener = db.collection("medicamentos")
                .whereEqualTo("emailPaciente", user.getEmail())
                .orderBy("fechaInicio", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        callback.onResult(new ArrayList<>());
                        return;
                    }

                    if (queryDocumentSnapshots == null) {
                        callback.onResult(new ArrayList<>());
                        return;
                    }

                    List<Medicamento> medicamentos = new ArrayList<>();
                    Date ahora = new Date();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Medicamento medicamento = new Medicamento();
                        medicamento.setId(doc.getId());
                        medicamento.setNombre(doc.getString("nombre"));
                        medicamento.setDosis(doc.getString("dosis"));
                        medicamento.setFrecuencia(doc.getString("frecuencia"));
                        medicamento.setMedico(doc.getString("medico"));
                        medicamento.setObservaciones(doc.getString("observaciones"));

                        // Convertir timestamps a Date
                        if (doc.getTimestamp("fechaInicio") != null) {
                            medicamento.setFechaInicio(doc.getTimestamp("fechaInicio").toDate());
                        }
                        if (doc.getTimestamp("fechaFin") != null) {
                            medicamento.setFechaFin(doc.getTimestamp("fechaFin").toDate());
                        }

                        // Determinar si está activo (dentro del rango de fechas)
                        Date fechaInicio = medicamento.getFechaInicio();
                        Date fechaFin = medicamento.getFechaFin();
                        if (fechaInicio != null && fechaFin != null) {
                            boolean activo = (ahora.compareTo(fechaInicio) >= 0) && (ahora.compareTo(fechaFin) <= 0);
                            medicamento.setActivo(activo);
                        } else {
                            medicamento.setActivo(false);
                        }

                        medicamentos.add(medicamento);
                    }

                    callback.onResult(medicamentos);
                });
    }

    /**
     * Detiene el listener de medicamentos
     */
    public void detenerListener() {
        if (medicamentosListener != null) {
            medicamentosListener.remove();
            medicamentosListener = null;
        }
    }

    public interface MedicamentosCallback {
        void onResult(List<Medicamento> medicamentos);
    }
}

