package com.example.proyectoandroid.util;

import android.util.Patterns;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utilidades para validación de datos
 */
public class ValidationUtils {

    /**
     * Valida si un email tiene formato válido
     */
    public static boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    /**
     * Valida si un teléfono chileno tiene formato válido (9 dígitos después de +56)
     */
    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        // Remover espacios y guiones
        String telefonoLimpio = telefono.trim().replaceAll("[\\s-]", "");
        // Debe tener 9 dígitos (después de +56)
        Pattern pattern = Pattern.compile("^\\+?56?[0-9]{9}$");
        return pattern.matcher(telefonoLimpio).matches();
    }

    /**
     * Valida si una fecha es futura (después de hoy)
     */
    public static boolean esFechaFutura(Date fecha) {
        if (fecha == null) {
            return false;
        }
        Date ahora = new Date();
        // Comparar solo fecha, sin hora
        return fecha.after(ahora);
    }

    /**
     * Valida si una fecha es futura o igual a hoy
     */
    public static boolean esFechaFuturaOIgual(Date fecha) {
        if (fecha == null) {
            return false;
        }
        Date ahora = new Date();
        // Comparar solo fecha, sin hora
        return !fecha.before(ahora);
    }

    /**
     * Valida si un nombre tiene formato válido (al menos 2 caracteres, solo letras y espacios)
     */
    public static boolean esNombreValido(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        String nombreLimpio = nombre.trim();
        // Al menos 2 caracteres, solo letras, espacios y algunos caracteres especiales comunes
        Pattern pattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,}$");
        return pattern.matcher(nombreLimpio).matches();
    }

    /**
     * Valida si una contraseña cumple con los requisitos mínimos
     * @param password La contraseña a validar
     * @param minLength Longitud mínima requerida
     * @return true si la contraseña es válida
     */
    public static boolean esPasswordValida(String password, int minLength) {
        if (password == null) {
            return false;
        }
        return password.length() >= minLength;
    }
}

