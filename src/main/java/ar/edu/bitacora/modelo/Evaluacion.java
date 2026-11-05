package ar.edu.bitacora.modelo;

import java.time.LocalDate;

public class Evaluacion {
    private int idEvaluacion;
    private int idDictado;
    private String titulo;
    private LocalDate fecha;
    private String tipo;

    // Datos que se leen con JOIN, solo para mostrar en pantalla
    private String materia;
    private String curso;

    public Evaluacion() {
    }

    public Evaluacion(int idDictado, String titulo, LocalDate fecha, String tipo) {
        this.idDictado = idDictado;
        this.titulo = titulo;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    public int getIdEvaluacion() { return idEvaluacion; }
    public void setIdEvaluacion(int idEvaluacion) { this.idEvaluacion = idEvaluacion; }

    public int getIdDictado() { return idDictado; }
    public void setIdDictado(int idDictado) { this.idDictado = idDictado; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    @Override
    public String toString() {
        return idEvaluacion + " | " + titulo + " (" + tipo + ") | " + fecha
                + " | " + materia + " | " + curso;
    }
}
