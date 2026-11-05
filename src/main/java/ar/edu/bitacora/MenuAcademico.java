package ar.edu.bitacora;

import ar.edu.bitacora.archivos.ImportadorCalificaciones;
import ar.edu.bitacora.dao.AsistenciaDAO;
import ar.edu.bitacora.dao.CalificacionDAO;
import ar.edu.bitacora.dao.CatalogoDAO;
import ar.edu.bitacora.dao.EvaluacionDAO;
import ar.edu.bitacora.dao.InscripcionDAO;
import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.excepciones.NotaInvalidaException;
import ar.edu.bitacora.excepciones.ReferenciaInvalidaException;
import ar.edu.bitacora.modelo.Asistencia;
import ar.edu.bitacora.modelo.Calificacion;
import ar.edu.bitacora.modelo.EstadoAsistencia;
import ar.edu.bitacora.modelo.Evaluacion;
import ar.edu.bitacora.modelo.Inscripcion;
import ar.edu.bitacora.reportes.Reporte;
import ar.edu.bitacora.reportes.ReporteAsistencia;
import ar.edu.bitacora.reportes.ReporteCalificaciones;
import ar.edu.bitacora.util.Estadistica;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

// Menú del módulo académico: inscripciones, evaluaciones, calificaciones y asistencia.
// Es la capa de "presentación": solo habla con el usuario y le pide el trabajo a los DAO.
public class MenuAcademico {

    private final Scanner teclado = new Scanner(System.in);
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final EvaluacionDAO evaluacionDAO = new EvaluacionDAO();
    private final CalificacionDAO calificacionDAO = new CalificacionDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final CatalogoDAO catalogo = new CatalogoDAO();
    private final ImportadorCalificaciones importador = new ImportadorCalificaciones();

    public static void main(String[] args) {
        new MenuAcademico().ejecutar();
    }

    public void ejecutar() {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Elegí una opción: ");
            try {
                switch (opcion) {
                    case 1:  inscribirEstudiante();         break;
                    case 2:  listarInscripciones();         break;
                    case 3:  crearEvaluacion();             break;
                    case 4:  listarEvaluaciones();          break;
                    case 5:  cargarCalificacion();          break;
                    case 6:  modificarCalificacion();       break;
                    case 7:  eliminarCalificacion();        break;
                    case 8:  verCalificacionesEvaluacion(); break;
                    case 9:  tomarAsistencia();             break;
                    case 10: verBoletin();                  break;
                    case 11: exportarReporte();             break;
                    case 12: importarCalificaciones();      break;
                    case 0:  System.out.println("Hasta luego."); break;
                    default: System.out.println("Opción inválida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (DatoDuplicadoException | ReferenciaInvalidaException | NotaInvalidaException e) {
                System.out.println("No se pudo completar la operación: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error con el archivo: " + e.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("===== BITÁCORA - Módulo académico =====");
        System.out.println(" 1. Inscribir un estudiante en un curso");
        System.out.println(" 2. Listar inscripciones de un curso");
        System.out.println(" 3. Crear una evaluación");
        System.out.println(" 4. Listar evaluaciones");
        System.out.println(" 5. Cargar una calificación");
        System.out.println(" 6. Modificar una calificación");
        System.out.println(" 7. Eliminar una calificación");
        System.out.println(" 8. Ver calificaciones de una evaluación (con estadísticas)");
        System.out.println(" 9. Tomar asistencia de un curso");
        System.out.println("10. Boletín de un estudiante (notas y asistencia)");
        System.out.println("11. Exportar un reporte a archivo CSV");
        System.out.println("12. Importar calificaciones desde un archivo CSV");
        System.out.println(" 0. Salir");
    }

    // ---------- Inscripciones ----------

    private void inscribirEstudiante()
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        System.out.println("--- Cursos disponibles ---");
        for (String linea : catalogo.listarCursos()) {
            System.out.println(linea);
        }
        String legajo = leerTexto("Legajo del estudiante: ");
        Integer idEstudiante = catalogo.buscarIdEstudiantePorLegajo(legajo);
        if (idEstudiante == null) {
            System.out.println("No existe un estudiante con ese legajo.");
            return;
        }
        int idCurso = leerEntero("Número de curso: ");

        Inscripcion nueva = new Inscripcion(idEstudiante, idCurso, LocalDate.now(), "ACTIVA");
        int id = inscripcionDAO.insertar(nueva);
        System.out.println("Inscripción guardada con el número " + id + ".");
    }

    private void listarInscripciones() throws SQLException {
        System.out.println("--- Cursos disponibles ---");
        for (String linea : catalogo.listarCursos()) {
            System.out.println(linea);
        }
        int idCurso = leerEntero("Número de curso: ");
        ArrayList<Inscripcion> lista = inscripcionDAO.listarPorCurso(idCurso);
        if (lista.isEmpty()) {
            System.out.println("Ese curso no tiene inscripciones.");
            return;
        }
        System.out.println("--- Inscripciones (número | legajo | estudiante | curso | estado) ---");
        for (Inscripcion i : lista) {
            System.out.println(i);
        }
        System.out.println("Total: " + lista.size());
    }

    // ---------- Evaluaciones ----------

    private void crearEvaluacion()
            throws SQLException, DatoDuplicadoException, ReferenciaInvalidaException {
        System.out.println("--- Dictados disponibles (número | materia | curso | docente) ---");
        for (String linea : catalogo.listarDictados()) {
            System.out.println(linea);
        }
        int idDictado = leerEntero("Número de dictado: ");
        String titulo = leerTexto("Título (ej: Segundo parcial): ");
        String tipo = leerTexto("Tipo (ej: Parcial, Trabajo práctico, Recuperatorio): ");
        LocalDate fecha = leerFecha("Fecha (AAAA-MM-DD, Enter para hoy): ");

        int id = evaluacionDAO.insertar(new Evaluacion(idDictado, titulo, fecha, tipo));
        System.out.println("Evaluación guardada con el número " + id + ".");
    }

    private void listarEvaluaciones() throws SQLException {
        ArrayList<Evaluacion> lista = evaluacionDAO.listar();
        if (lista.isEmpty()) {
            System.out.println("Todavía no hay evaluaciones.");
            return;
        }
        System.out.println("--- Evaluaciones (número | título | fecha | materia | curso) ---");
        for (Evaluacion ev : lista) {
            System.out.println(ev);
        }
    }

    // ---------- Calificaciones ----------

    private void cargarCalificacion() throws SQLException, DatoDuplicadoException,
            ReferenciaInvalidaException, NotaInvalidaException {
        listarEvaluaciones();
        int idEvaluacion = leerEntero("Número de evaluación: ");
        String legajo = leerTexto("Legajo del estudiante: ");

        Inscripcion inscripcion = inscripcionDAO.buscarPorLegajoYEvaluacion(legajo, idEvaluacion);
        if (inscripcion == null) {
            System.out.println("Ese legajo no existe o no está inscripto en el curso de la evaluación.");
            return;
        }
        double nota = leerDecimal("Nota (0 a 10): ");
        String observacion = leerTexto("Observación (puede quedar vacía): ");

        Calificacion c = new Calificacion(idEvaluacion, inscripcion.getIdInscripcion(), nota, observacion);
        int id = calificacionDAO.insertar(c);
        System.out.println("Calificación guardada con el número " + id + ".");
    }

    private void modificarCalificacion() throws SQLException, DatoDuplicadoException,
            ReferenciaInvalidaException, NotaInvalidaException {
        int id = leerEntero("Número de calificación a modificar: ");
        Calificacion c = calificacionDAO.buscarPorId(id);
        if (c == null) {
            System.out.println("No existe una calificación con ese número.");
            return;
        }
        System.out.println("Actual: " + c);
        c.setNota(leerDecimal("Nota nueva (0 a 10): "));
        c.setObservacion(leerTexto("Observación nueva: "));

        if (calificacionDAO.actualizar(c)) {
            System.out.println("Calificación modificada.");
        } else {
            System.out.println("No se modificó nada.");
        }
    }

    private void eliminarCalificacion() throws SQLException, ReferenciaInvalidaException {
        int id = leerEntero("Número de calificación a eliminar: ");
        Calificacion c = calificacionDAO.buscarPorId(id);
        if (c == null) {
            System.out.println("No existe una calificación con ese número.");
            return;
        }
        System.out.println("Se va a eliminar: " + c);
        if (!confirmar("¿Confirmás? (S/N): ")) {
            System.out.println("Operación cancelada.");
            return;
        }
        if (calificacionDAO.eliminar(id)) {
            System.out.println("Calificación eliminada.");
        } else {
            System.out.println("No se eliminó nada.");
        }
    }

    private void verCalificacionesEvaluacion() throws SQLException {
        listarEvaluaciones();
        int idEvaluacion = leerEntero("Número de evaluación: ");

        // Las notas llegan de la base en un ArrayList (no sabemos cuántas son)...
        ArrayList<Calificacion> lista = calificacionDAO.listarPorEvaluacion(idEvaluacion);
        if (lista.isEmpty()) {
            System.out.println("Esa evaluación todavía no tiene calificaciones.");
            return;
        }
        for (Calificacion c : lista) {
            System.out.println(c);
        }

        // ...y se pasan a un arreglo para calcular las estadísticas
        double[] notas = Estadistica.aArreglo(lista);
        System.out.println("--- Estadísticas ---");
        System.out.println("Cantidad de notas: " + notas.length);
        System.out.println("Promedio: " + String.format("%.2f", Estadistica.promedio(notas)));
        System.out.println("Nota más alta: " + String.format("%.2f", Estadistica.maximo(notas)));
        System.out.println("Nota más baja: " + String.format("%.2f", Estadistica.minimo(notas)));
        System.out.println("Aprobados (" + Estadistica.NOTA_APROBACION + " o más): "
                + Estadistica.contarAprobados(notas) + " de " + notas.length);

        double[] ordenadas = Estadistica.ordenarDeMayorAMenor(notas);
        System.out.print("Mejores notas:");
        for (int i = 0; i < ordenadas.length && i < 3; i++) {
            System.out.print(" " + String.format("%.2f", ordenadas[i]));
        }
        System.out.println();
    }

    // ---------- Asistencia ----------

    private void tomarAsistencia() throws SQLException, ReferenciaInvalidaException {
        System.out.println("--- Cursos disponibles ---");
        for (String linea : catalogo.listarCursos()) {
            System.out.println(linea);
        }
        int idCurso = leerEntero("Número de curso: ");
        LocalDate fecha = leerFecha("Fecha de la clase (AAAA-MM-DD, Enter para hoy): ");

        // Solo se toma lista a los estudiantes con inscripción ACTIVA
        ArrayList<Inscripcion> activos = new ArrayList<>();
        for (Inscripcion i : inscripcionDAO.listarPorCurso(idCurso)) {
            if (i.getEstado().equals("ACTIVA")) {
                activos.add(i);
            }
        }
        if (activos.isEmpty()) {
            System.out.println("Ese curso no tiene estudiantes con inscripción activa.");
            return;
        }

        System.out.println("Para cada estudiante: P = presente, A = ausente, J = justificada, T = tardanza.");
        ArrayList<Asistencia> guardadas = new ArrayList<>();
        for (Inscripcion i : activos) {
            EstadoAsistencia estado = leerEstado(i.getApellido() + ", " + i.getNombre()
                    + " (" + i.getLegajo() + ") [P]: ");
            Asistencia a = new Asistencia(i.getIdInscripcion(), fecha, estado, "");
            try {
                asistenciaDAO.insertar(a);
                guardadas.add(a);
            } catch (DatoDuplicadoException e) {
                System.out.println("   Ya tenía la asistencia de esa fecha cargada.");
            }
        }

        // Resumen con un arreglo: una posición por cada estado
        int[] conteo = Estadistica.contarAsistencias(guardadas);
        System.out.println("--- Resumen de la clase del " + fecha + " ---");
        EstadoAsistencia[] estados = EstadoAsistencia.values();
        for (int k = 0; k < estados.length; k++) {
            System.out.println(estados[k] + ": " + conteo[k]);
        }
    }

    private void verBoletin() throws SQLException {
        int idInscripcion = leerEntero("Número de inscripción (se ve en la opción 2): ");
        Inscripcion inscripcion = inscripcionDAO.buscarPorId(idInscripcion);
        if (inscripcion == null) {
            System.out.println("No existe una inscripción con ese número.");
            return;
        }
        System.out.println("===== Boletín =====");
        System.out.println("Estudiante: " + inscripcion.getApellido() + ", " + inscripcion.getNombre()
                + " (legajo " + inscripcion.getLegajo() + ")");
        System.out.println("Curso: " + inscripcion.getCurso());

        ArrayList<Calificacion> notas = calificacionDAO.listarPorInscripcion(idInscripcion);
        System.out.println("--- Calificaciones ---");
        if (notas.isEmpty()) {
            System.out.println("Sin calificaciones cargadas.");
        } else {
            for (Calificacion c : notas) {
                System.out.println(c.getTituloEvaluacion() + ": " + String.format("%.2f", c.getNota()));
            }
            double promedio = Estadistica.promedio(Estadistica.aArreglo(notas));
            System.out.println("Promedio: " + String.format("%.2f", promedio));
        }

        ArrayList<Asistencia> asistencias = asistenciaDAO.listarPorInscripcion(idInscripcion);
        System.out.println("--- Asistencia ---");
        if (asistencias.isEmpty()) {
            System.out.println("Sin asistencias cargadas.");
        } else {
            int[] conteo = Estadistica.contarAsistencias(asistencias);
            EstadoAsistencia[] estados = EstadoAsistencia.values();
            for (int k = 0; k < estados.length; k++) {
                System.out.println(estados[k] + ": " + conteo[k]);
            }
            System.out.println("Presentismo: "
                    + String.format("%.1f", Estadistica.porcentajePresentismo(conteo)) + " %");
        }
    }

    // ---------- Archivos ----------

    private void exportarReporte() throws SQLException, IOException {
        System.out.println("1. Calificaciones de una evaluación");
        System.out.println("2. Asistencias de una inscripción");
        int tipo = leerEntero("¿Qué reporte querés exportar? ");

        Reporte reporte;
        String nombreArchivo;
        if (tipo == 1) {
            int id = leerEntero("Número de evaluación: ");
            reporte = new ReporteCalificaciones(id);
            nombreArchivo = "calificaciones_evaluacion_" + id + ".csv";
        } else if (tipo == 2) {
            int id = leerEntero("Número de inscripción: ");
            reporte = new ReporteAsistencia(id);
            nombreArchivo = "asistencia_inscripcion_" + id + ".csv";
        } else {
            System.out.println("Opción inválida.");
            return;
        }

        reporte.mostrar();
        String ruta = reporte.exportarCSV(nombreArchivo);
        System.out.println("Archivo guardado en: " + ruta);
    }

    private void importarCalificaciones() throws IOException {
        System.out.println("El archivo debe tener una línea de títulos y luego: legajo;nota;observacion");
        int idEvaluacion = leerEntero("Número de evaluación a la que pertenecen las notas: ");
        String ruta = leerTexto("Ruta del archivo (Enter para datos/calificaciones_ejemplo.csv): ");
        if (ruta.isEmpty()) {
            ruta = "datos/calificaciones_ejemplo.csv";
        }

        ArrayList<String> detalle = new ArrayList<>();
        int[] resultado = importador.importar(ruta, idEvaluacion, detalle);

        for (String linea : detalle) {
            System.out.println(linea);
        }
        System.out.println("--- Resultado: " + resultado[0] + " guardadas, " + resultado[1] + " con error ---");
    }

    // ---------- Lectura de datos por teclado ----------

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim();
    }

    // Repite la pregunta hasta que se escriba un número entero
    private int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Ingresá solo números enteros.");
            }
        }
    }

    // Repite la pregunta hasta que se escriba un número (acepta coma o punto)
    private double leerDecimal(String mensaje) {
        while (true) {
            try {
                return Double.parseDouble(leerTexto(mensaje).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingresá un número, por ejemplo 7,5.");
            }
        }
    }

    // Si se aprieta Enter sin escribir nada, devuelve la fecha de hoy
    private LocalDate leerFecha(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Usá el formato AAAA-MM-DD, por ejemplo 2026-10-27.");
            }
        }
    }

    // Si se aprieta Enter sin escribir nada, se toma como PRESENTE
    private EstadoAsistencia leerEstado(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).toUpperCase();
            if (texto.isEmpty() || texto.equals("P")) {
                return EstadoAsistencia.PRESENTE;
            } else if (texto.equals("A")) {
                return EstadoAsistencia.AUSENTE;
            } else if (texto.equals("J")) {
                return EstadoAsistencia.JUSTIFICADA;
            } else if (texto.equals("T")) {
                return EstadoAsistencia.TARDANZA;
            }
            System.out.println("Escribí P, A, J o T.");
        }
    }

    private boolean confirmar(String mensaje) {
        String respuesta = leerTexto(mensaje).toUpperCase();
        return respuesta.equals("S");
    }
}
