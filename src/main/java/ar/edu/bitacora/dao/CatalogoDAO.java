package ar.edu.bitacora.dao;

import ar.edu.bitacora.config.ConexionMySQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Consultas de solo lectura que el menú necesita para mostrar opciones
// (cursos, dictados) y para buscar un estudiante por su legajo.
public class CatalogoDAO {

    // Devuelve una línea de texto por cada curso
    public ArrayList<String> listarCursos() throws SQLException {
        String sql = "SELECT id_curso, nombre, anio_lectivo, division FROM cursos ORDER BY id_curso";
        ArrayList<String> lineas = new ArrayList<>();
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lineas.add(rs.getInt("id_curso") + " | " + rs.getString("nombre") + " "
                        + rs.getInt("anio_lectivo") + " \"" + rs.getString("division") + "\"");
            }
        }
        return lineas;
    }

    // Devuelve una línea de texto por cada dictado (materia + curso + docente)
    public ArrayList<String> listarDictados() throws SQLException {
        String sql = "SELECT d.id_dictado, m.nombre AS materia, c.nombre AS curso, c.division, "
                   + "       doc.apellido, doc.nombre AS nombre_docente "
                   + "FROM dictados d "
                   + "JOIN materias m ON m.id_materia = d.id_materia "
                   + "JOIN cursos c ON c.id_curso = d.id_curso "
                   + "JOIN docentes doc ON doc.id_docente = d.id_docente "
                   + "ORDER BY d.id_dictado";
        ArrayList<String> lineas = new ArrayList<>();
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lineas.add(rs.getInt("id_dictado") + " | " + rs.getString("materia") + " | "
                        + rs.getString("curso") + " " + rs.getString("division")
                        + " | Docente: " + rs.getString("apellido") + ", " + rs.getString("nombre_docente"));
            }
        }
        return lineas;
    }

    // Devuelve el id del estudiante con ese legajo, o null si no existe
    public Integer buscarIdEstudiantePorLegajo(String legajo) throws SQLException {
        String sql = "SELECT id_estudiante FROM estudiantes WHERE legajo = ?";
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, legajo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_estudiante");
                }
            }
        }
        return null;
    }
}
