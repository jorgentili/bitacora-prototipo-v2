package ar.edu.bitacora.dao;

import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.excepciones.ReferenciaInvalidaException;
import ar.edu.bitacora.modelo.Asistencia;
import ar.edu.bitacora.modelo.EstadoAsistencia;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AsistenciaDAO extends BaseDAO<Asistencia> {

    // Consulta base: une la asistencia con la inscripción y el estudiante
    private static final String SELECT_BASE =
            "SELECT a.id_asistencia, a.id_inscripcion, a.fecha, a.estado, a.observacion, "
          + "       e.legajo, e.apellido, e.nombre "
          + "FROM asistencias a "
          + "JOIN inscripciones i ON i.id_inscripcion = a.id_inscripcion "
          + "JOIN estudiantes e ON e.id_estudiante = i.id_estudiante ";

    @Override
    protected String nombreEntidad() {
        return "una asistencia de ese estudiante en esa fecha";
    }

    @Override
    protected Asistencia armarObjeto(ResultSet rs) throws SQLException {
        Asistencia a = new Asistencia();
        a.setIdAsistencia(rs.getInt("id_asistencia"));
        a.setIdInscripcion(rs.getInt("id_inscripcion"));
        a.setFecha(rs.getDate("fecha").toLocalDate());
        a.setEstado(EstadoAsistencia.valueOf(rs.getString("estado")));
        a.setObservacion(rs.getString("observacion"));
        a.setLegajo(rs.getString("legajo"));
        a.setApellido(rs.getString("apellido"));
        a.setNombre(rs.getString("nombre"));
        return a;
    }

    @Override
    public int insertar(Asistencia a)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        String sql = "INSERT INTO asistencias (id_inscripcion, fecha, estado, observacion) "
                   + "VALUES (?, ?, ?, ?)";
        try {
            return ejecutarInsert(sql, a.getIdInscripcion(), Date.valueOf(a.getFecha()),
                    a.getEstado().name(), a.getObservacion());
        } catch (SQLException ex) {
            controlarError(ex);
            return -1;
        }
    }

    @Override
    public ArrayList<Asistencia> listar() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY a.fecha, e.apellido, e.nombre");
    }

    @Override
    public Asistencia buscarPorId(int id) throws SQLException {
        return consultarUno(SELECT_BASE + "WHERE a.id_asistencia = ?", id);
    }

    // Todas las asistencias de un estudiante en un curso
    public ArrayList<Asistencia> listarPorInscripcion(int idInscripcion) throws SQLException {
        return consultar(SELECT_BASE + "WHERE a.id_inscripcion = ? ORDER BY a.fecha", idInscripcion);
    }

    @Override
    public boolean actualizar(Asistencia a)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        String sql = "UPDATE asistencias SET estado = ?, observacion = ? WHERE id_asistencia = ?";
        try {
            return ejecutarCambio(sql, a.getEstado().name(), a.getObservacion(), a.getIdAsistencia()) > 0;
        } catch (SQLException ex) {
            controlarError(ex);
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException, ReferenciaInvalidaException {
        try {
            return ejecutarCambio("DELETE FROM asistencias WHERE id_asistencia = ?", id) > 0;
        } catch (SQLException ex) {
            try {
                controlarError(ex);
            } catch (DatoDuplicadoException dup) {
                throw ex;
            }
            return false;
        }
    }
}
