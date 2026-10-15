package ar.edu.bitacora.modelo;

import java.time.LocalDate;

// Estudiante "es una" Persona: con "extends" hereda nombre, apellido, dni y email
// (con sus get y set) sin tener que volver a escribirlos.
public class Estudiante extends Persona {

    // Acá quedan solo los datos propios del estudiante
    private int idEstudiante;
    private String legajo;
    private LocalDate fechaNacimiento;
    private boolean activo = true;

    public Estudiante() {
        super(); // llama al constructor vacío de Persona
    }

    public Estudiante(int idEstudiante, String legajo, String nombre, String apellido,
                      String dni, String email, LocalDate fechaNacimiento, boolean activo) {
        // super(...) le pasa a Persona los datos que le corresponden a ella
        super(nombre, apellido, dni, email);
        // el resto lo guarda Estudiante
        this.idEstudiante = idEstudiante;
        this.legajo = legajo;
        this.fechaNacimiento = fechaNacimiento;
        this.activo = activo;
    }

    // Implementación del método abstracto de Persona: obligatoria.
    @Override
    public String getRol() {
        return "Estudiante";
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}