package ar.edu.bitacora.dao;

import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.excepciones.ReferenciaInvalidaException;
import ar.edu.bitacora.modelo.Inscripcion;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InscripcionDAO extends BaseDAO<Inscripcion> {

    // Consulta base: une la inscripción con el estudiante y el curso
    private static final String SELECT_BASE =
            "SELECT i.id_inscripcion, i.id_estudiante, i.id_curso, i.fecha_inscripcion, i.estado, "
          + "       e.legajo, e.apellido, e.nombre, c.nombre AS curso, c.division "
          + "FROM inscripciones i "
          + "JOIN estudiantes e ON e.id_estudiante = i.id_estudiante "
          + "JOIN cursos c ON c.id_curso = i.id_curso ";

    @Override
    protected String nombreEntidad() {
        return "una inscripción de ese estudiante en ese curso";
    }

    @Override
    protected Inscripcion armarObjeto(ResultSet rs) throws SQLException {
        Inscripcion i = new Inscripcion();
        i.setIdInscripcion(rs.getInt("id_inscripcion"));
        i.setIdEstudiante(rs.getInt("id_estudiante"));
        i.setIdCurso(rs.getInt("id_curso"));
        i.setFechaInscripcion(rs.getDate("fecha_inscripcion").toLocalDate());
        i.setEstado(rs.getString("estado"));
        i.setLegajo(rs.getString("legajo"));
        i.setApellido(rs.getString("apellido"));
        i.setNombre(rs.getString("nombre"));
        i.setCurso(rs.getString("curso") + " " + rs.getString("division"));
        return i;
    }

    @Override
    public int insertar(Inscripcion i)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        String sql = "INSERT INTO inscripciones (id_estudiante, id_curso, fecha_inscripcion, estado) "
                   + "VALUES (?, ?, ?, ?)";
        try {
            return ejecutarInsert(sql, i.getIdEstudiante(), i.getIdCurso(),
                    Date.valueOf(i.getFechaInscripcion()), i.getEstado());
        } catch (SQLException ex) {
            controlarError(ex);
            return -1;
        }
    }

    @Override
    public ArrayList<Inscripcion> listar() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY c.nombre, c.division, e.apellido, e.nombre");
    }

    @Override
    public Inscripcion buscarPorId(int id) throws SQLException {
        return consultarUno(SELECT_BASE + "WHERE i.id_inscripcion = ?", id);
    }

    // Los estudiantes inscriptos en un curso
    public ArrayList<Inscripcion> listarPorCurso(int idCurso) throws SQLException {
        return consultar(SELECT_BASE + "WHERE i.id_curso = ? ORDER BY e.apellido, e.nombre", idCurso);
    }

    // Busca la inscripción activa de un estudiante (por legajo) en el curso donde se dicta una evaluación.
    // Se usa al importar notas desde un archivo. Devuelve null si no la encuentra.
    public Inscripcion buscarPorLegajoYEvaluacion(String legajo, int idEvaluacion) throws SQLException {
        String sql = SELECT_BASE
                + "JOIN dictados d ON d.id_curso = i.id_curso "
                + "JOIN evaluaciones ev ON ev.id_dictado = d.id_dictado "
                + "WHERE e.legajo = ? AND ev.id_evaluacion = ? AND i.estado = 'ACTIVA'";
        return consultarUno(sql, legajo, idEvaluacion);
    }

    // Solo se puede cambiar el estado de la inscripción (ACTIVA, FINALIZADA, CANCELADA)
    @Override
    public boolean actualizar(Inscripcion i)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        try {
            return ejecutarCambio("UPDATE inscripciones SET estado = ? WHERE id_inscripcion = ?",
                    i.getEstado(), i.getIdInscripcion()) > 0;
        } catch (SQLException ex) {
            controlarError(ex);
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException, ReferenciaInvalidaException {
        try {
            return ejecutarCambio("DELETE FROM inscripciones WHERE id_inscripcion = ?", id) > 0;
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
