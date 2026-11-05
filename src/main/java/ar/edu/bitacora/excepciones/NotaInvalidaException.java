package ar.edu.bitacora.excepciones;

// Se usa cuando una nota está fuera del rango de 0 a 10.
public class NotaInvalidaException extends Exception {
    public NotaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
