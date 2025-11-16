package com.example.proyectoandroid.util;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

/**
 * Maneja errores y los convierte en mensajes amigables para el usuario
 */
public class ErrorHandler {
    private static final String TAG = "ErrorHandler";

    // Mapa de códigos de error de Firebase Auth a mensajes amigables
    private static final Map<String, String> AUTH_ERROR_MESSAGES = new HashMap<>();

    static {
        AUTH_ERROR_MESSAGES.put("ERROR_INVALID_EMAIL", "El correo electrónico no es válido");
        AUTH_ERROR_MESSAGES.put("ERROR_WRONG_PASSWORD", "Contraseña incorrecta");
        AUTH_ERROR_MESSAGES.put("ERROR_USER_NOT_FOUND", "Usuario no encontrado. Verifica tu RUT y contraseña");
        AUTH_ERROR_MESSAGES.put("ERROR_USER_DISABLED", "Esta cuenta ha sido deshabilitada");
        AUTH_ERROR_MESSAGES.put("ERROR_TOO_MANY_REQUESTS", "Demasiados intentos. Intenta más tarde");
        AUTH_ERROR_MESSAGES.put("ERROR_OPERATION_NOT_ALLOWED", "Esta operación no está permitida");
        AUTH_ERROR_MESSAGES.put("ERROR_EMAIL_ALREADY_IN_USE", "Este correo electrónico ya está registrado");
        AUTH_ERROR_MESSAGES.put("ERROR_WEAK_PASSWORD", "La contraseña es muy débil. Debe tener al menos 6 caracteres");
        AUTH_ERROR_MESSAGES.put("ERROR_INVALID_CREDENTIAL", "Credenciales incorrectas. Verifica tu RUT y contraseña");
        AUTH_ERROR_MESSAGES.put("ERROR_NETWORK_REQUEST_FAILED", "Error de conexión. Verifica tu internet");
    }

    // Mapa de códigos de error de Firestore a mensajes amigables
    private static final Map<String, String> FIRESTORE_ERROR_MESSAGES = new HashMap<>();

    static {
        FIRESTORE_ERROR_MESSAGES.put("PERMISSION_DENIED", "No tienes permiso para realizar esta acción");
        FIRESTORE_ERROR_MESSAGES.put("UNAVAILABLE", "Servicio no disponible. Intenta más tarde");
        FIRESTORE_ERROR_MESSAGES.put("DEADLINE_EXCEEDED", "La operación tardó demasiado. Intenta nuevamente");
        FIRESTORE_ERROR_MESSAGES.put("UNAUTHENTICATED", "Debes iniciar sesión para realizar esta acción");
        FIRESTORE_ERROR_MESSAGES.put("NOT_FOUND", "No se encontró la información solicitada");
        FIRESTORE_ERROR_MESSAGES.put("ALREADY_EXISTS", "Este registro ya existe");
        FIRESTORE_ERROR_MESSAGES.put("RESOURCE_EXHAUSTED", "Límite de operaciones alcanzado. Intenta más tarde");
        FIRESTORE_ERROR_MESSAGES.put("FAILED_PRECONDITION", "La operación no se puede completar en este momento");
        FIRESTORE_ERROR_MESSAGES.put("ABORTED", "La operación fue cancelada");
        FIRESTORE_ERROR_MESSAGES.put("OUT_OF_RANGE", "Los datos proporcionados están fuera del rango válido");
        FIRESTORE_ERROR_MESSAGES.put("UNIMPLEMENTED", "Esta funcionalidad aún no está disponible");
        FIRESTORE_ERROR_MESSAGES.put("INTERNAL", "Error interno del servidor. Intenta más tarde");
        FIRESTORE_ERROR_MESSAGES.put("DATA_LOSS", "Error al procesar los datos");
    }

    /**
     * Obtiene un mensaje amigable para el usuario basado en la excepción
     */
    public static String getFriendlyMessage(Exception e) {
        if (e == null) {
            return "Error inesperado. Intenta nuevamente.";
        }

        // Manejar errores de Firebase Auth
        if (e instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) e;
            String errorCode = authException.getErrorCode();
            String message = AUTH_ERROR_MESSAGES.get(errorCode);
            
            if (message != null) {
                return message;
            }
            
            // Mensajes específicos por tipo de excepción
            if (e instanceof FirebaseAuthInvalidUserException) {
                return "Usuario no encontrado. Verifica tu RUT y contraseña";
            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                return "Credenciales incorrectas. Verifica tu RUT y contraseña";
            } else if (e instanceof FirebaseAuthWeakPasswordException) {
                return "La contraseña es muy débil. Debe tener al menos 6 caracteres";
            } else if (e instanceof FirebaseAuthUserCollisionException) {
                return "Este correo electrónico ya está registrado";
            }
            
            // Si no hay mensaje específico, usar el mensaje de la excepción
            return authException.getMessage() != null ? authException.getMessage() : "Error de autenticación";
        }

        // Manejar errores de Firestore
        if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException firestoreException = (FirebaseFirestoreException) e;
            String errorCode = firestoreException.getCode().name();
            String message = FIRESTORE_ERROR_MESSAGES.get(errorCode);
            
            if (message != null) {
                return message;
            }
            
            return "Error al guardar los datos. Intenta nuevamente.";
        }

        // Manejar errores de red
        String errorMessage = e.getMessage();
        if (errorMessage != null) {
            if (errorMessage.contains("network") || errorMessage.contains("Network") || 
                errorMessage.contains("internet") || errorMessage.contains("Internet") ||
                errorMessage.contains("connection") || errorMessage.contains("Connection")) {
                return "Error de conexión. Verifica tu internet e intenta nuevamente.";
            }
        }

        // Log del error para debugging
        Log.e(TAG, "Error no manejado: " + e.getClass().getSimpleName(), e);

        // Mensaje genérico
        return "Error inesperado. Intenta nuevamente.";
    }

    /**
     * Obtiene un mensaje amigable basado en el código de error de Firebase Auth
     */
    public static String getAuthErrorMessage(String errorCode) {
        return AUTH_ERROR_MESSAGES.getOrDefault(errorCode, "Error de autenticación");
    }

    /**
     * Obtiene un mensaje amigable basado en el código de error de Firestore
     */
    public static String getFirestoreErrorMessage(String errorCode) {
        return FIRESTORE_ERROR_MESSAGES.getOrDefault(errorCode, "Error al guardar los datos");
    }
}

