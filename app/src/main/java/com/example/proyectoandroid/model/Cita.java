package com.example.proyectoandroid.model;

import java.util.Date;

/**
 * Modelo de datos de una cita médica
 */
public class Cita {
    private String id;
    private Date fecha;
    private String hora;
    private String motivo;
    private String medico;
    private String estado; // "pendiente", "confirmada", "cancelada"
    private String tipo; // "consulta", "control", "examen"

    public Cita() {
    }

    public Cita(String id, Date fecha, String hora, String motivo, String medico, String estado, String tipo) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.medico = medico;
        this.estado = estado;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMedico() {
        return medico;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

