package ar.edu.bitacora.excepciones;

// Se usa cuando un dato apunta a algo que no existe (clave foránea)
// o cuando se quiere borrar algo que otros datos están usando.
public class ReferenciaInvalidaException extends Exception {
    public ReferenciaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
