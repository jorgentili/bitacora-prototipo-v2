package ar.edu.bitacora.modelo;

import java.time.LocalDate;

public class Asistencia {
    private int idAsistencia;
    private int idInscripcion;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String observacion;

    // Datos que se leen con JOIN, solo para mostrar en pantalla
    private String legajo;
    private String apellido;
    private String nombre;

    public Asistencia() {
    }

    public Asistencia(int idInscripcion, LocalDate fecha, EstadoAsistencia estado, String observacion) {
        this.idInscripcion = idInscripcion;
        this.fecha = fecha;
        this.estado = estado;
        this.observacion = observacion;
    }

    public int getIdAsistencia() { return idAsistencia; }
    public void setIdAsistencia(int idAsistencia) { this.idAsistencia = idAsistencia; }

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public EstadoAsistencia getEstado() { return estado; }
    public void setEstado(EstadoAsistencia estado) { this.estado = estado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return idAsistencia + " | " + fecha + " | " + legajo + " | " + apellido + ", " + nombre
                + " | " + estado;
    }
}
