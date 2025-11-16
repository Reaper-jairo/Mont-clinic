package com.example.proyectoandroid.model;

/**
 * Modelo de usuario
 */
public class User {
    private String rut;
    private String email;
    private String uid;

    public User() {
    }

    public User(String rut, String email, String uid) {
        this.rut = rut;
        this.email = email;
        this.uid = uid;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

