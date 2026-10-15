package ar.edu.bitacora;

import ar.edu.bitacora.dao.EstudianteDAO;
import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.modelo.Estudiante;

import java.sql.SQLException;

// Prueba del manejo de excepciones: intenta cargar un DNI que ya existe.
public class PruebaExcepciones {

    public static void main(String[] args) {

        EstudianteDAO dao = new EstudianteDAO();

        // El DNI 40111222 ya lo tiene Jorge Gentili en la base.
        Estudiante repetido = new Estudiante();
        repetido.setLegajo("ALU-777");
        repetido.setNombre("Prueba");
        repetido.setApellido("Repetido");
        repetido.setDni("40111222");

        try {
            System.out.println("Intentando insertar un estudiante con DNI repetido...");
            dao.insertar(repetido);

            // Si llegó hasta acá, el DNI no estaba repetido: borramos lo que se creó.
            System.out.println("Se insertó (el DNI no estaba repetido). Lo borro.");
            dao.eliminar(repetido.getIdEstudiante());

        } catch (DatoDuplicadoException ex) {
            // Nuestra excepción: mensaje claro para el usuario.
            System.out.println("Error controlado: " + ex.getMessage());

        } catch (SQLException ex) {
            // Cualquier otro problema de base de datos.
            System.out.println("Error de base de datos: " + ex.getMessage());

        } finally {
            // Se ejecuta siempre, haya habido error o no.
            System.out.println("Fin de la prueba (bloque finally).");
        }
    }
}