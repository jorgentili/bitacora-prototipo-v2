package ar.edu.bitacora.modelo;

import java.time.LocalDate;

public class Inscripcion {
    private int idInscripcion;
    private int idEstudiante;
    private int idCurso;
    private LocalDate fechaInscripcion;
    private String estado;   // ACTIVA, FINALIZADA o CANCELADA

    // Datos que se leen con JOIN, solo para mostrar en pantalla
    private String legajo;
    private String apellido;
    private String nombre;
    private String curso;

    public Inscripcion() {
    }

    public Inscripcion(int idEstudiante, int idCurso, LocalDate fechaInscripcion, String estado) {
        this.idEstudiante = idEstudiante;
        this.idCurso = idCurso;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    @Override
    public String toString() {
        return idInscripcion + " | " + legajo + " | " + apellido + ", " + nombre
                + " | " + curso + " | " + estado;
    }
}
