package ar.edu.bitacora.dao;

import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.excepciones.ReferenciaInvalidaException;
import java.sql.SQLException;
import java.util.List;

// Interfaz del patrón DAO: define QUÉ operaciones tiene que ofrecer
// cualquier clase que guarde datos en la base. Cada DAO decide CÓMO las hace.
// La <T> significa "el tipo de objeto que maneja" (Calificacion, Asistencia, etc.).
public interface Dao<T> {

    // Guarda un objeto nuevo y devuelve el id que le asignó MySQL
    int insertar(T objeto) throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException;

    // Devuelve todos los registros
    List<T> listar() throws SQLException;

    // Devuelve un registro por su id, o null si no existe
    T buscarPorId(int id) throws SQLException;

    // Modifica un registro. Devuelve true si se modificó algo
    boolean actualizar(T objeto) throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException;

    // Borra un registro. Devuelve true si realmente se borró algo
    boolean eliminar(int id) throws SQLException, ReferenciaInvalidaException;
}
