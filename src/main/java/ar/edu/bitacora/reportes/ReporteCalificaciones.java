package ar.edu.bitacora.reportes;

import ar.edu.bitacora.dao.CalificacionDAO;
import ar.edu.bitacora.modelo.Calificacion;
import java.sql.SQLException;
import java.util.ArrayList;

// Reporte con las notas de una evaluación
public class ReporteCalificaciones extends Reporte {

    private final int idEvaluacion;
    private final CalificacionDAO dao = new CalificacionDAO();

    public ReporteCalificaciones(int idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    @Override
    protected String titulo() {
        return "Calificaciones de la evaluación " + idEvaluacion;
    }

    @Override
    protected String[] encabezados() {
        return new String[] {"Legajo", "Apellido", "Nombre", "Evaluación", "Nota", "Observación"};
    }

    @Override
    protected ArrayList<String[]> filas() throws SQLException {
        ArrayList<String[]> filas = new ArrayList<>();
        for (Calificacion c : dao.listarPorEvaluacion(idEvaluacion)) {
            filas.add(new String[] {
                c.getLegajo(), c.getApellido(), c.getNombre(), c.getTituloEvaluacion(),
                String.format("%.2f", c.getNota()), c.getObservacion()
            });
        }
        return filas;
    }
}
