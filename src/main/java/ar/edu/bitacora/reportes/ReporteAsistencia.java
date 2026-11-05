package ar.edu.bitacora.reportes;

import ar.edu.bitacora.dao.AsistenciaDAO;
import ar.edu.bitacora.modelo.Asistencia;
import java.sql.SQLException;
import java.util.ArrayList;

// Reporte con las asistencias de un estudiante en un curso
public class ReporteAsistencia extends Reporte {

    private final int idInscripcion;
    private final AsistenciaDAO dao = new AsistenciaDAO();

    public ReporteAsistencia(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    @Override
    protected String titulo() {
        return "Asistencias de la inscripción " + idInscripcion;
    }

    @Override
    protected String[] encabezados() {
        return new String[] {"Fecha", "Legajo", "Apellido", "Nombre", "Estado", "Observación"};
    }

    @Override
    protected ArrayList<String[]> filas() throws SQLException {
        ArrayList<String[]> filas = new ArrayList<>();
        for (Asistencia a : dao.listarPorInscripcion(idInscripcion)) {
            filas.add(new String[] {
                a.getFecha().toString(), a.getLegajo(), a.getApellido(), a.getNombre(),
                a.getEstado().name(), a.getObservacion()
            });
        }
        return filas;
    }
}
