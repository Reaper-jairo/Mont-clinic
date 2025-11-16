package com.example.proyectoandroid.model;

/**
 * Modelo para validación de RUT
 */
public class RutValidation {
    private String rut;
    private boolean isValid;
    private String errorMessage;

    public RutValidation(String rut, boolean isValid, String errorMessage) {
        this.rut = rut;
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    public String getRut() {
        return rut;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

