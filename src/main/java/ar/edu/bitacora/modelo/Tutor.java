package ar.edu.bitacora.modelo;

// Tutor "es una" Persona: hereda nombre, apellido, dni y email.
public class Tutor extends Persona {

    // Solo los datos propios del tutor
    private int idTutor;
    private String telefono;
    private boolean activo = true;

    public Tutor() {
        super();
    }

    public Tutor(int idTutor, String nombre, String apellido, String dni,
                 String email, String telefono, boolean activo) {
        super(nombre, apellido, dni, email);
        this.idTutor = idTutor;
        this.telefono = telefono;
        this.activo = activo;
    }

    // Tercera versión de getRol(): cada hija responde distinto.
    @Override
    public String getRol() {
        return "Tutor";
    }

    public int getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(int idTutor) {
        this.idTutor = idTutor;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}