package com.example.proyectoandroid.model;

/**
 * Modelo de datos del paciente
 */
public class Paciente {
    private String nombre;
    private String rut;
    private String email;
    private String telefono;
    private String direccion;

    public Paciente() {
    }

    public Paciente(String nombre, String rut, String email, String telefono, String direccion) {
        this.nombre = nombre;
        this.rut = rut;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

