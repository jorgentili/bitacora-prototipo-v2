package ar.edu.bitacora.dao;

import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.excepciones.ReferenciaInvalidaException;
import ar.edu.bitacora.modelo.Evaluacion;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EvaluacionDAO extends BaseDAO<Evaluacion> {

    // Consulta base: une la evaluación con su dictado (materia y curso)
    private static final String SELECT_BASE =
            "SELECT ev.id_evaluacion, ev.id_dictado, ev.titulo, ev.fecha, ev.tipo, "
          + "       m.nombre AS materia, c.nombre AS curso, c.division "
          + "FROM evaluaciones ev "
          + "JOIN dictados d ON d.id_dictado = ev.id_dictado "
          + "JOIN materias m ON m.id_materia = d.id_materia "
          + "JOIN cursos c ON c.id_curso = d.id_curso ";

    @Override
    protected String nombreEntidad() {
        return "una evaluación";
    }

    @Override
    protected Evaluacion armarObjeto(ResultSet rs) throws SQLException {
        Evaluacion ev = new Evaluacion();
        ev.setIdEvaluacion(rs.getInt("id_evaluacion"));
        ev.setIdDictado(rs.getInt("id_dictado"));
        ev.setTitulo(rs.getString("titulo"));
        ev.setFecha(rs.getDate("fecha").toLocalDate());
        ev.setTipo(rs.getString("tipo"));
        ev.setMateria(rs.getString("materia"));
        ev.setCurso(rs.getString("curso") + " " + rs.getString("division"));
        return ev;
    }

    @Override
    public int insertar(Evaluacion ev)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        String sql = "INSERT INTO evaluaciones (id_dictado, titulo, fecha, tipo) VALUES (?, ?, ?, ?)";
        try {
            return ejecutarInsert(sql, ev.getIdDictado(), ev.getTitulo(), Date.valueOf(ev.getFecha()),
                    ev.getTipo());
        } catch (SQLException ex) {
            controlarError(ex);
            return -1;
        }
    }

    @Override
    public ArrayList<Evaluacion> listar() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY ev.fecha DESC, ev.id_evaluacion DESC");
    }

    @Override
    public Evaluacion buscarPorId(int id) throws SQLException {
        return consultarUno(SELECT_BASE + "WHERE ev.id_evaluacion = ?", id);
    }

    @Override
    public boolean actualizar(Evaluacion ev)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        String sql = "UPDATE evaluaciones SET titulo = ?, fecha = ?, tipo = ? WHERE id_evaluacion = ?";
        try {
            return ejecutarCambio(sql, ev.getTitulo(), Date.valueOf(ev.getFecha()), ev.getTipo(),
                    ev.getIdEvaluacion()) > 0;
        } catch (SQLException ex) {
            controlarError(ex);
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) throws SQLException, ReferenciaInvalidaException {
        try {
            return ejecutarCambio("DELETE FROM evaluaciones WHERE id_evaluacion = ?", id) > 0;
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
