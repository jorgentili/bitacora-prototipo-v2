package ar.edu.bitacora.dao;

import ar.edu.bitacora.config.ConexionMySQL;
import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.modelo.Estudiante;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    // ---------- INSERTAR ----------
    // "throws" avisa que este método puede lanzar estas dos excepciones.
    public void insertar(Estudiante e) throws SQLException, DatoDuplicadoException {

        String sql = "INSERT INTO estudiantes (legajo, nombre, apellido, dni, email, fecha_nacimiento, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, e.getLegajo());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getApellido());
            ps.setString(4, e.getDni());

            if (e.getEmail() == null || e.getEmail().isBlank()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, e.getEmail());
            }

            if (e.getFechaNacimiento() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, Date.valueOf(e.getFechaNacimiento()));
            }

            ps.setBoolean(7, e.isActivo());

            ps.executeUpdate();

            try (ResultSet idsGenerados = ps.getGeneratedKeys()) {
                if (idsGenerados.next()) {
                    e.setIdEstudiante(idsGenerados.getInt(1));
                }
            }

        } catch (SQLException ex) {
            // El código 1062 es el que usa MySQL para "dato duplicado".
            // Lo traducimos a nuestra excepción, con un mensaje entendible.
            if (ex.getErrorCode() == 1062) {
                throw new DatoDuplicadoException(
                        "Ya existe un estudiante con ese DNI, legajo o email.");
            }
            // Cualquier otro error de base de datos se vuelve a lanzar tal cual.
            throw ex;
        }
    }

    // ---------- CONSULTAR ----------
    public List<Estudiante> listar() throws SQLException {

        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes ORDER BY apellido, nombre";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date fecha = rs.getDate("fecha_nacimiento");

                Estudiante e = new Estudiante(
                        rs.getInt("id_estudiante"),
                        rs.getString("legajo"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getString("email"),
                        fecha == null ? null : fecha.toLocalDate(),
                        rs.getBoolean("activo")
                );

                lista.add(e);
            }
        }

        return lista;
    }

    // ---------- BORRAR ----------
    public void eliminar(int idEstudiante) throws SQLException {

        String sql = "DELETE FROM estudiantes WHERE id_estudiante = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.executeUpdate();
        }
    }
}