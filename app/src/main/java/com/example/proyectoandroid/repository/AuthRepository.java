package com.example.proyectoandroid.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Repositorio para manejar autenticación y consultas de RUT
 */
public class AuthRepository {
    private static AuthRepository instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public interface EmailCallback {
        void onResult(String email);
    }

    public interface AuthCallback {
        void onResult(AuthResult result);
    }

    private AuthRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    /**
     * Obtiene el usuario actual autenticado
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /**
     * Busca el email asociado a un RUT usando callback
     */
    public void buscarEmailPorRut(String rutNormalizado, EmailCallback callback) {
        // Asegurar que el RUT esté en mayúsculas
        String rutBuscar = rutNormalizado != null ? rutNormalizado.toUpperCase() : "";
        
        // Intentar buscar con el RUT tal cual
        db.collection("rut_index").document(rutBuscar).get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        String email = doc.getString("email");
                        if (email != null && !email.trim().isEmpty()) {
                            callback.onResult(email.trim());
                            return;
                        }
                    }
                    // Si no se encontró, intentar en minúsculas (por si acaso)
                    if (!rutBuscar.equals(rutNormalizado.toLowerCase())) {
                        db.collection("rut_index").document(rutNormalizado.toLowerCase()).get()
                                .addOnSuccessListener(doc2 -> {
                                    if (doc2 != null && doc2.exists()) {
                                        String email2 = doc2.getString("email");
                                        if (email2 != null && !email2.trim().isEmpty()) {
                                            callback.onResult(email2.trim());
                                        } else {
                                            callback.onResult(null);
                                        }
                                    } else {
                                        callback.onResult(null);
                                    }
                                })
                                .addOnFailureListener(e -> callback.onResult(null));
                    } else {
                        callback.onResult(null);
                    }
                })
                .addOnFailureListener(e -> callback.onResult(null));
    }

    /**
     * Inicia sesión con email y contraseña usando callback
     */
    public void iniciarSesion(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        callback.onResult(new AuthResult(true, user, null));
                    } else {
                        callback.onResult(new AuthResult(false, null, "Usuario no encontrado"));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onResult(new AuthResult(false, null, e.getMessage()));
                });
    }

    /**
     * Cierra sesión
     */
    public void cerrarSesion() {
        auth.signOut();
    }

    /**
     * Clase para encapsular el resultado de autenticación
     */
    public static class AuthResult {
        private final boolean success;
        private final FirebaseUser user;
        private final String error;

        public AuthResult(boolean success, FirebaseUser user, String error) {
            this.success = success;
            this.user = user;
            this.error = error;
        }

        public boolean isSuccess() {
            return success;
        }

        public FirebaseUser getUser() {
            return user;
        }

        public String getError() {
            return error;
        }
    }
}

