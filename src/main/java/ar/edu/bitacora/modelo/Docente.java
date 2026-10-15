package ar.edu.bitacora.modelo;

// Docente "es una" Persona: hereda nombre, apellido, dni y email.
public class Docente extends Persona {

    // Solo los datos propios del docente
    private int idDocente;
    private String legajo;
    private boolean activo = true;

    public Docente() {
        super();
    }

    public Docente(int idDocente, String legajo, String nombre, String apellido,
                   String dni, String email, boolean activo) {
        super(nombre, apellido, dni, email);
        this.idDocente = idDocente;
        this.legajo = legajo;
        this.activo = activo;
    }

    // Cada clase hija responde a su manera: acá el rol es "Docente".
    @Override
    public String getRol() {
        return "Docente";
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}