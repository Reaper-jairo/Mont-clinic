package com.example.proyectoandroid.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utilidades para manejo de fechas y formatos
 */
public class DateUtils {

    // Formatos de fecha
    public static final String FORMAT_DISPLAY = "dd/MM/yyyy";
    public static final String FORMAT_STORAGE = "yyyy-MM-dd";
    public static final String FORMAT_TIME = "HH:mm";
    public static final String FORMAT_DATETIME_DISPLAY = "dd/MM/yyyy HH:mm";
    public static final String FORMAT_DATETIME_STORAGE = "yyyy-MM-dd HH:mm";

    /**
     * Formatea una fecha para mostrar al usuario (dd/MM/yyyy)
     */
    public static String formatDateForDisplay(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DISPLAY, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Formatea una fecha para almacenar (yyyy-MM-dd)
     */
    public static String formatDateForStorage(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_STORAGE, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Formatea una hora para mostrar (HH:mm)
     */
    public static String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIME, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Parsea una fecha desde formato de display (dd/MM/yyyy)
     */
    public static Date parseDateFromDisplay(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DISPLAY, Locale.getDefault());
            return sdf.parse(dateStr.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parsea una fecha desde formato de storage (yyyy-MM-dd)
     */
    public static Date parseDateFromStorage(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_STORAGE, Locale.getDefault());
            return sdf.parse(dateStr.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parsea una fecha intentando ambos formatos (display y storage)
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        
        // Intentar formato display primero (dd/MM/yyyy)
        if (dateStr.contains("/")) {
            Date date = parseDateFromDisplay(dateStr);
            if (date != null) return date;
        }
        
        // Intentar formato storage (yyyy-MM-dd)
        Date date = parseDateFromStorage(dateStr);
        if (date != null) return date;
        
        return null;
    }

    /**
     * Convierte una fecha de display a storage
     */
    public static String convertDisplayToStorage(String displayDate) {
        Date date = parseDateFromDisplay(displayDate);
        if (date == null) return displayDate;
        return formatDateForStorage(date);
    }

    /**
     * Convierte una fecha de storage a display
     */
    public static String convertStorageToDisplay(String storageDate) {
        Date date = parseDateFromStorage(storageDate);
        if (date == null) return storageDate;
        return formatDateForDisplay(date);
    }

    /**
     * Combina fecha y hora en un objeto Date
     */
    public static Date combineDateAndTime(Date date, String timeStr) {
        if (date == null) return null;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        if (timeStr != null && !timeStr.trim().isEmpty()) {
            String[] partes = timeStr.trim().split(":");
            if (partes.length == 2) {
                try {
                    int horas = Integer.parseInt(partes[0]);
                    int minutos = Integer.parseInt(partes[1]);
                    cal.set(Calendar.HOUR_OF_DAY, horas);
                    cal.set(Calendar.MINUTE, minutos);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                } catch (NumberFormatException e) {
                    // Si no se puede parsear, usar solo la fecha
                }
            }
        }
        
        return cal.getTime();
    }

    /**
     * Obtiene la fecha actual
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * Obtiene la fecha actual formateada para display
     */
    public static String getCurrentDateDisplay() {
        return formatDateForDisplay(getCurrentDate());
    }

    /**
     * Obtiene la hora actual formateada
     */
    public static String getCurrentTime() {
        return formatTime(getCurrentDate());
    }
}

