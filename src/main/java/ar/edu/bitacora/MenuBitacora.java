package ar.edu.bitacora;

import ar.edu.bitacora.dao.DocenteDAO;
import ar.edu.bitacora.dao.EstudianteDAO;
import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.modelo.Estudiante;
import ar.edu.bitacora.modelo.Persona;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Menú de consola del sistema BITÁCORA.
public class MenuBitacora {

    private static final Scanner teclado = new Scanner(System.in);
    private static final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private static final DocenteDAO docenteDAO = new DocenteDAO();

    public static void main(String[] args) {

        int opcion = -1;

        // Bucle de repetición: el menú se repite hasta que el usuario elige 0.
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Elegí una opción: ");

            // Estructura condicional múltiple.
            switch (opcion) {
                case 1:
                    altaEstudiante();
                    break;
                case 2:
                    listarEstudiantes();
                    break;
                case 3:
                    buscarPorDni();
                    break;
                case 4:
                    eliminarEstudiante();
                    break;
                case 5:
                    listarPersonas();
                    break;
                case 0:
                    System.out.println("Hasta luego.");
                    break;
                default:
                    System.out.println("Opción inválida. Probá de nuevo.");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== BITÁCORA - Gestión Educativa =====");
        System.out.println("1. Alta de estudiante");
        System.out.println("2. Listar estudiantes (ordenados por apellido)");
        System.out.println("3. Buscar estudiante por DNI");
        System.out.println("4. Eliminar estudiante");
        System.out.println("5. Listar estudiantes y docentes (polimorfismo)");
        System.out.println("0. Salir");
    }

    // ---------- Lectura segura de datos ----------

    // Lee un número entero. Si el usuario escribe letras, atrapa la excepción
    // y vuelve a pedir el dato en vez de cortar el programa.
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = teclado.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Eso no es un número válido.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = teclado.nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("Este dato es obligatorio.");
            }
        } while (texto.isEmpty());
        return texto;
    }

    // ---------- Opciones del menú ----------

    private static void altaEstudiante() {
        System.out.println("--- Alta de estudiante ---");

        String legajo = leerTexto("Legajo: ");
        String nombre = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        String dni = leerTexto("DNI: ");
        String email = leerTexto("Email: ");

        LocalDate fecha = null;
        while (fecha == null) {
            String texto = leerTexto("Fecha de nacimiento (AAAA-MM-DD): ");
            try {
                fecha = LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Ejemplo: 2009-05-15");
            }
        }

        // Creación de objeto usando el constructor completo.
        Estudiante nuevo = new Estudiante(0, legajo, nombre, apellido, dni, email, fecha, true);

        try {
            estudianteDAO.insertar(nuevo);
            System.out.println("Estudiante guardado con id " + nuevo.getIdEstudiante());
        } catch (DatoDuplicadoException e) {
            System.out.println("No se pudo guardar: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private static void listarEstudiantes() {
        try {
            List<Estudiante> lista = new ArrayList<>(estudianteDAO.listar());

            if (lista.isEmpty()) {
                System.out.println("No hay estudiantes cargados.");
                return;
            }

            ordenarPorApellido(lista);

            System.out.println("--- Estudiantes ---");
            for (Estudiante e : lista) {
                System.out.println(e.getIdEstudiante() + " | " + e.getLegajo() + " | " + e);
            }
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    // Algoritmo de ordenación: ordenamiento por selección, escrito a mano.
    // Busca el menor apellido del tramo que falta ordenar y lo pone en su lugar.
    private static void ordenarPorApellido(List<Estudiante> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < lista.size(); j++) {
                String a = lista.get(j).getApellido();
                String b = lista.get(menor).getApellido();
                if (a.compareToIgnoreCase(b) < 0) {
                    menor = j;
                }
            }
            Estudiante auxiliar = lista.get(i);
            lista.set(i, lista.get(menor));
            lista.set(menor, auxiliar);
        }
    }

    // Algoritmo de búsqueda: búsqueda secuencial por DNI.
    private static void buscarPorDni() {
        String dni = leerTexto("DNI a buscar: ");

        try {
            List<Estudiante> lista = estudianteDAO.listar();
            Estudiante encontrado = null;

            for (Estudiante e : lista) {
                if (e.getDni().equals(dni)) {
                    encontrado = e;
                    break;
                }
            }

            if (encontrado != null) {
                System.out.println("Encontrado: " + encontrado);
            } else {
                System.out.println("No hay ningún estudiante con ese DNI.");
            }
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private static void eliminarEstudiante() {
        int id = leerEntero("ID del estudiante a eliminar: ");

        try {
            estudianteDAO.eliminar(id);
            System.out.println("Operación realizada (si el id existía, se eliminó).");
        } catch (SQLException e) {
            // Si tiene inscripciones, MySQL rechaza el borrado por la clave foránea.
            System.out.println("No se pudo eliminar: " + e.getMessage());
        }
    }

    // Polimorfismo: una lista de Persona con estudiantes y docentes mezclados.
    private static void listarPersonas() {
        try {
            List<Persona> personas = new ArrayList<>();
            personas.addAll(estudianteDAO.listar());
            personas.addAll(docenteDAO.listar());

            System.out.println("--- Personas del sistema ---");
            for (Persona p : personas) {
                System.out.println(p.getRol() + ": " + p);
            }
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }
}