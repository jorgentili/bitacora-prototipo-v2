package ar.edu.bitacora.dao;

import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.excepciones.NotaInvalidaException;
import ar.edu.bitacora.excepciones.ReferenciaInvalidaException;
import ar.edu.bitacora.modelo.Calificacion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CalificacionDAO extends BaseDAO<Calificacion> {

    // Consulta base: une calificaciones con evaluaciones, inscripciones y estudiantes
    private static final String SELECT_BASE =
            "SELECT c.id_calificacion, c.id_evaluacion, c.id_inscripcion, c.nota, c.observacion, "
          + "       ev.titulo, e.legajo, e.apellido, e.nombre "
          + "FROM calificaciones c "
          + "JOIN evaluaciones ev ON ev.id_evaluacion = c.id_evaluacion "
          + "JOIN inscripciones i ON i.id_inscripcion = c.id_inscripcion "
          + "JOIN estudiantes e ON e.id_estudiante = i.id_estudiante ";

    @Override
    protected String nombreEntidad() {
        return "una calificación para ese estudiante en esa evaluación";
    }

    @Override
    protected Calificacion armarObjeto(ResultSet rs) throws SQLException {
        Calificacion c = new Calificacion();
        c.setIdCalificacion(rs.getInt("id_calificacion"));
        c.setIdEvaluacion(rs.getInt("id_evaluacion"));
        c.setIdInscripcion(rs.getInt("id_inscripcion"));
        try {
            c.setNota(rs.getDouble("nota"));
        } catch (NotaInvalidaException e) {
            // No debería pasar porque la base ya controla de 0 a 10
            throw new SQLException("La base devolvió una nota fuera de rango", e);
        }
        c.setObservacion(rs.getString("observacion"));
        c.setTituloEvaluacion(rs.getString("titulo"));
        c.setLegajo(rs.getString("legajo"));
        c.setApellido(rs.getString("apellido"));
        c.setNombre(rs.getString("nombre"));
        return c;
    }

    @Override
    public int insertar(Calificacion c)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        String sql = "INSERT INTO calificaciones (id_evaluacion, id_inscripcion, nota, observacion) "
                   + "VALUES (?, ?, ?, ?)";
        try {
            return ejecutarInsert(sql, c.getIdEvaluacion(), c.getIdInscripcion(),
                    c.getNota(), c.getObservacion());
        } catch (SQLException ex) {
            controlarError(ex);
            return -1; // no llega acá: controlarError siempre lanza una excepción
        }
    }

    @Override
    public ArrayList<Calificacion> listar() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY ev.fecha, e.apellido, e.nombre");
    }

    @Override
    public Calificacion buscarPorId(int id) throws SQLException {
        return consultarUno(SELECT_BASE + "WHERE c.id_calificacion = ?", id);
    }

    // Todas las notas de una evaluación (por ejemplo, de un parcial)
    public ArrayList<Calificacion> listarPorEvaluacion(int idEvaluacion) throws SQLException {
        return consultar(SELECT_BASE + "WHERE c.id_evaluacion = ? ORDER BY e.apellido, e.nombre",
                idEvaluacion);
    }

    // Todas las notas de un estudiante en un curso (su boletín)
    public ArrayList<Calificacion> listarPorInscripcion(int idInscripcion) throws SQLException {
        return consultar(SELECT_BASE + "WHERE c.id_inscripcion = ? ORDER BY ev.fecha",
                idInscripcion);
    }

    @Override
    public boolean actualizar(Calificacion c)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        String sql = "UPDATE calificaciones SET nota = ?, observacion = ? WHERE id_calificacion = ?";
        try {
            return ejecutarCambio(sql, c.getNota(), c.getObservacion(), c.getIdCalificacion()) > 0;
        } catch (SQLException ex) {
            controlarError(ex);
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException, ReferenciaInvalidaException {
        try {
            return ejecutarCambio("DELETE FROM calificaciones WHERE id_calificacion = ?", id) > 0;
        } catch (SQLException ex) {
            try {
                controlarError(ex);
            } catch (DatoDuplicadoException dup) {
                // Un DELETE nunca da "duplicado"; esto es solo porque controlarError lo declara
                throw ex;
            }
            return false;
        }
    }
}
