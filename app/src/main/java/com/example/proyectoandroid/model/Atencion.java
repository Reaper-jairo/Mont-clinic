package com.example.proyectoandroid.model;

import java.util.Date;

/**
 * Modelo de datos de una atención médica
 */
public class Atencion {
    private String id;
    private Date fecha;
    private String motivo;
    private String medico;
    private String diagnostico;
    private String observaciones;

    public Atencion() {
    }

    public Atencion(String id, Date fecha, String motivo, String medico, String diagnostico, String observaciones) {
        this.id = id;
        this.fecha = fecha;
        this.motivo = motivo;
        this.medico = medico;
        this.diagnostico = diagnostico;
        this.observaciones = observaciones;
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

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}

