package ar.edu.bitacora.archivos;

import ar.edu.bitacora.dao.CalificacionDAO;
import ar.edu.bitacora.dao.InscripcionDAO;
import ar.edu.bitacora.excepciones.DatoDuplicadoException;
import ar.edu.bitacora.excepciones.NotaInvalidaException;
import ar.edu.bitacora.excepciones.ReferenciaInvalidaException;
import ar.edu.bitacora.modelo.Calificacion;
import ar.edu.bitacora.modelo.Inscripcion;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

// Lee un archivo CSV con notas (legajo;nota;observacion) y las guarda en la base.
// Si una línea tiene un problema, la informa y sigue con la siguiente.
public class ImportadorCalificaciones {

    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final CalificacionDAO calificacionDAO = new CalificacionDAO();

    // Devuelve un arreglo con dos números: [0] líneas guardadas, [1] líneas con error.
    // El detalle de cada línea se agrega al ArrayList "detalle".
    public int[] importar(String ruta, int idEvaluacion, ArrayList<String> detalle) throws IOException {
        int[] resultado = new int[2];

        try (BufferedReader lector = Files.newBufferedReader(Paths.get(ruta), StandardCharsets.UTF_8)) {
            String linea;
            int numero = 0;
            while ((linea = lector.readLine()) != null) {
                numero++;
                linea = linea.replace("﻿", "").trim();
                if (numero == 1 || linea.isEmpty()) {
                    continue; // la primera línea tiene los títulos de las columnas
                }
                String mensaje = procesarLinea(linea, idEvaluacion);
                if (mensaje == null) {
                    resultado[0]++;
                    detalle.add("Línea " + numero + ": guardada (" + linea + ")");
                } else {
                    resultado[1]++;
                    detalle.add("Línea " + numero + ": ERROR - " + mensaje);
                }
            }
        }
        return resultado;
    }

    // Procesa una línea. Devuelve null si salió bien, o el mensaje de error si falló.
    private String procesarLinea(String linea, int idEvaluacion) {
        String[] campos = linea.split(";");
        if (campos.length < 2) {
            return "faltan datos (se espera legajo;nota;observacion)";
        }
        String legajo = campos[0].trim();
        String observacion = "";
        if (campos.length >= 3) {
            observacion = campos[2].trim();
        }

        try {
            double nota = Double.parseDouble(campos[1].trim().replace(',', '.'));

            Inscripcion inscripcion = inscripcionDAO.buscarPorLegajoYEvaluacion(legajo, idEvaluacion);
            if (inscripcion == null) {
                return "el legajo " + legajo + " no existe o no está inscripto en el curso de la evaluación";
            }

            Calificacion c = new Calificacion(idEvaluacion, inscripcion.getIdInscripcion(), nota, observacion);
            calificacionDAO.insertar(c);
            return null;

        } catch (NumberFormatException e) {
            return "la nota \"" + campos[1].trim() + "\" no es un número";
        } catch (NotaInvalidaException | DatoDuplicadoException | ReferenciaInvalidaException e) {
            return e.getMessage();
        } catch (SQLException e) {
            return "error de base de datos: " + e.getMessage();
        }
    }
}
