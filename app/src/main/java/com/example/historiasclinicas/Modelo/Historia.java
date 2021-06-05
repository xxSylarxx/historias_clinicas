package com.example.historiasclinicas.Modelo;

import java.io.Serializable;

public class Historia implements Serializable {
    private String fecha;
    private String hora;
    private String horaDiagnosticada;
    private String idAtencion;
    private String key;
    private String codPaciente;
    private String cuenta;
    private String doctor;
    private String diagnostico;
    private String recomendaciones;
    private String laboratorio;
    private String rayosx;
    private String radiologia;
    private String nombrePaciente;
    private String apellidoPaciente;
    private int atendido=0;
    private int edad;
    private double peso;
    private double talla;
    private double presion;
    private double temperatura;


    public Historia() {
    }

    public String getHoraDiagnosticada() {
        return horaDiagnosticada;
    }

    public void setHoraDiagnosticada(String horaDiagnosticada) {
        this.horaDiagnosticada = horaDiagnosticada;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRadiologia() {
        return radiologia;
    }

    public void setRadiologia(String radiologia) {
        this.radiologia = radiologia;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getApellidoPaciente() {
        return apellidoPaciente;
    }

    public void setApellidoPaciente(String apellidoPaciente) {
        this.apellidoPaciente = apellidoPaciente;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(String idAtencion) {
        this.idAtencion = idAtencion;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getTalla() {
        return talla;
    }

    public void setTalla(double talla) {
        this.talla = talla;
    }

    public double getPresion() {
        return presion;
    }

    public void setPresion(double presion) {
        this.presion = presion;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public int getAtendido() {
        return atendido;
    }

    public void setAtendido(int atendido) {
        this.atendido = atendido;
    }

    public String getCodPaciente() {
        return codPaciente;
    }

    public void setCodPaciente(String codPaciente) {
        this.codPaciente = codPaciente;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getRecomendaciones() {
        return recomendaciones;
    }

    public void setRecomendaciones(String recomendaciones) {
        this.recomendaciones = recomendaciones;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getRayosx() {
        return rayosx;
    }

    public void setRayosx(String rayosx) {
        this.rayosx = rayosx;
    }
}
