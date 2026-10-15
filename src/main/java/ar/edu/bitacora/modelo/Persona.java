package ar.edu.bitacora.modelo;

// Clase abstracta: agrupa lo que Estudiante, Docente y Tutor tienen en común.
// No se puede instanciar directamente ("new Persona(...)" no es válido).
public abstract class Persona {

    private String nombre;
    private String apellido;
    private String dni;
    private String email;

    // Constructor vacío, para que las clases hijas puedan llamar a super()
    // sin tener todos los datos todavía.
    public Persona() {
    }

    public Persona(String nombre, String apellido, String dni, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Método abstracto: no tiene cuerpo. Cada clase hija está OBLIGADA
    // a decir "cuál es mi rol" a su manera. Esto es la base del polimorfismo.
    public abstract String getRol();

    // Método normal (no abstracto): todas las clases hijas lo heredan
    // tal cual, sin tener que reescribirlo.
    @Override
    public String toString() {
        return apellido + ", " + nombre + " (DNI " + dni + ") - " + getRol();
    }
}