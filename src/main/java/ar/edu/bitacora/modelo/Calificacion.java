package ar.edu.bitacora.modelo;

import ar.edu.bitacora.excepciones.NotaInvalidaException;

public class Calificacion {
    private int idCalificacion;
    private int idEvaluacion;
    private int idInscripcion;
    private double nota;
    private String observacion;

    // Datos que se leen con JOIN, solo para mostrar en pantalla
    private String tituloEvaluacion;
    private String legajo;
    private String apellido;
    private String nombre;

    public Calificacion() {
    }

    public Calificacion(int idEvaluacion, int idInscripcion, double nota, String observacion)
            throws NotaInvalidaException {
        this.idEvaluacion = idEvaluacion;
        this.idInscripcion = idInscripcion;
        setNota(nota);
        this.observacion = observacion;
    }

    public int getIdCalificacion() { return idCalificacion; }
    public void setIdCalificacion(int idCalificacion) { this.idCalificacion = idCalificacion; }

    public int getIdEvaluacion() { return idEvaluacion; }
    public void setIdEvaluacion(int idEvaluacion) { this.idEvaluacion = idEvaluacion; }

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public double getNota() { return nota; }

    // La validación está en el set: así ninguna nota fuera de rango entra al sistema.
    public void setNota(double nota) throws NotaInvalidaException {
        if (nota < 0 || nota > 10) {
            throw new NotaInvalidaException("La nota debe estar entre 0 y 10 (se ingresó " + nota + ").");
        }
        this.nota = nota;
    }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getTituloEvaluacion() { return tituloEvaluacion; }
    public void setTituloEvaluacion(String tituloEvaluacion) { this.tituloEvaluacion = tituloEvaluacion; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return idCalificacion + " | " + legajo + " | " + apellido + ", " + nombre
                + " | " + tituloEvaluacion + " | nota: " + String.format("%.2f", nota);
    }
}
