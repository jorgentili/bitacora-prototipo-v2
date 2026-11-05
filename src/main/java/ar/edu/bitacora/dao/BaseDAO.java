package ar.edu.bitacora.dao;

import ar.edu.bitacora.config.ConexionMySQL;
import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.excepciones.ReferenciaInvalidaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// Clase abstracta: tiene el código que se repetía en todos los DAO
// (abrir la conexión, ejecutar la consulta, traducir los errores de MySQL).
// Cada DAO hijo solo tiene que escribir su SQL y cómo armar su objeto.
public abstract class BaseDAO<T> implements Dao<T> {

    // Códigos de error de MySQL que nos interesan
    private static final int ERROR_DUPLICADO = 1062;           // dato repetido (UNIQUE)
    private static final int ERROR_SIN_REFERENCIA = 1452;      // la clave foránea no existe
    private static final int ERROR_CON_DATOS_ASOCIADOS = 1451; // lo quieren borrar y otros datos lo usan

    // Lo completa cada hijo. Ejemplo: "una calificación"
    protected abstract String nombreEntidad();

    // Lo completa cada hijo: convierte una fila de la base en un objeto
    protected abstract T armarObjeto(ResultSet rs) throws SQLException;

    // Convierte el error técnico de MySQL en una excepción nuestra, con un mensaje claro.
    // Siempre lanza algo, por eso el que la llama no necesita seguir después.
    protected void controlarError(SQLException ex)
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        int codigo = ex.getErrorCode();
        if (codigo == ERROR_DUPLICADO) {
            throw new DatoDuplicadoException("Ya existe " + nombreEntidad() + " con esos datos.");
        }
        if (codigo == ERROR_SIN_REFERENCIA) {
            throw new ReferenciaInvalidaException(
                    "Alguno de los datos relacionados no existe (estudiante, curso, evaluación...).");
        }
        if (codigo == ERROR_CON_DATOS_ASOCIADOS) {
            throw new ReferenciaInvalidaException(
                    "No se puede borrar " + nombreEntidad() + " porque otros datos la están usando.");
        }
        throw ex;
    }

    // Ejecuta un SELECT y devuelve los resultados en un ArrayList.
    // "Object... parametros" significa que se pueden pasar 0, 1 o más valores para los signos ?
    protected ArrayList<T> consultar(String sql, Object... parametros) throws SQLException {
        ArrayList<T> lista = new ArrayList<>();
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            cargarParametros(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(armarObjeto(rs));
                }
            }
        }
        return lista;
    }

    // Igual que consultar, pero devuelve solo el primero (o null si no hay ninguno)
    protected T consultarUno(String sql, Object... parametros) throws SQLException {
        ArrayList<T> lista = consultar(sql, parametros);
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    // Ejecuta un INSERT y devuelve el id generado por MySQL
    protected int ejecutarInsert(String sql, Object... parametros) throws SQLException {
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cargarParametros(ps, parametros);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
            }
        }
        return -1;
    }

    // Ejecuta un UPDATE o DELETE y devuelve cuántas filas cambiaron
    protected int ejecutarCambio(String sql, Object... parametros) throws SQLException {
        try (Connection con = ConexionMySQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            cargarParametros(ps, parametros);
            return ps.executeUpdate();
        }
    }

    // Reemplaza cada ? de la consulta por el valor que corresponde
    private void cargarParametros(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            ps.setObject(i + 1, parametros[i]);
        }
    }
}
