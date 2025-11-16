package com.example.proyectoandroid.util;

import android.text.TextUtils;

/**
 * Utilidades para validación y normalización de RUT
 */
public class RutUtils {

    /**
     * Normaliza un RUT eliminando guiones, puntos y espacios, y convirtiendo a mayúsculas
     */
    public static String normalizarRut(String raw) {
        if (raw == null) return "";
        return raw.replace("-", "").replace(".", "").replace(" ", "").toUpperCase();
    }

    /**
     * Valida un RUT sin guión (formato básico: 7-8 dígitos + 1 dígito verificador)
     */
    public static boolean esRutValidoSinGuion(String rutSinGuion) {
        if (rutSinGuion == null || rutSinGuion.length() < 2) return false;
        
        // El RUT debe tener entre 8 y 9 caracteres (7-8 dígitos + 1 dígito verificador)
        if (rutSinGuion.length() < 8 || rutSinGuion.length() > 9) return false;
        
        char dv = rutSinGuion.charAt(rutSinGuion.length() - 1);
        String cuerpo = rutSinGuion.substring(0, rutSinGuion.length() - 1);
        
        if (cuerpo.isEmpty()) return false;
        
        // Validar que el cuerpo tenga 7 u 8 dígitos
        if (cuerpo.length() < 7 || cuerpo.length() > 8) return false;
        
        // Validar que el cuerpo solo tenga dígitos
        for (int i = 0; i < cuerpo.length(); i++) {
            if (!Character.isDigit(cuerpo.charAt(i))) return false;
        }
        
        // Validar que el dígito verificador sea válido (0-9 o K)
        if (!(Character.isDigit(dv) || dv == 'K')) return false;
        
        // Formato válido (no validamos el algoritmo del dígito verificador)
        return true;
    }
    
    /**
     * Valida un RUT sin guión y verifica que el dígito verificador sea correcto según el algoritmo
     */
    public static boolean esRutValidoConAlgoritmo(String rutSinGuion) {
        if (!esRutValidoSinGuion(rutSinGuion)) return false;
        
        char dv = rutSinGuion.charAt(rutSinGuion.length() - 1);
        String cuerpo = rutSinGuion.substring(0, rutSinGuion.length() - 1);
        
        // Validar que el dígito verificador sea correcto según el algoritmo
        return calcularDV(cuerpo).equals(String.valueOf(dv));
    }
    
    /**
     * Obtiene el dígito verificador correcto para un RUT
     */
    public static String obtenerDVCorrecto(String cuerpo) {
        if (cuerpo == null || cuerpo.isEmpty()) return "";
        return calcularDV(cuerpo);
    }

    /**
     * Calcula el dígito verificador de un RUT
     */
    public static String calcularDV(String cuerpo) {
        int suma = 0, factor = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * factor;
            factor = (factor == 7) ? 2 : factor + 1;
        }
        int dv = 11 - (suma % 11);
        if (dv == 11) return "0";
        if (dv == 10) return "K";
        return String.valueOf(dv);
    }

    /**
     * Valida que un RUT tenga formato mínimo válido
     */
    public static boolean tieneFormatoMinimo(String rut) {
        return rut != null && rut.length() >= 2;
    }
}

