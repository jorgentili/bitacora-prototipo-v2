package ar.edu.bitacora.excepciones;

// Excepción propia: se lanza cuando se intenta guardar un dato que ya existe
// (DNI, legajo o email repetidos). Al extender Exception, es una excepción
// "controlada": Java obliga a quien la use a atraparla o declararla.
public class DatoDuplicadoException extends Exception {

    // El mensaje se lo pasamos al constructor de Exception con super(...)
    public DatoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}