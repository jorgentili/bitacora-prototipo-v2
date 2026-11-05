package ar.edu.bitacora.reportes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

// Clase abstracta con el "esqueleto" de un reporte (patrón Template Method):
// los pasos para mostrar o guardar son siempre los mismos, y cada reporte
// hijo solo dice su título, sus columnas y sus filas.
public abstract class Reporte {

    // Lo que completa cada reporte hijo
    protected abstract String titulo();
    protected abstract String[] encabezados();
    protected abstract ArrayList<String[]> filas() throws SQLException;

    // Muestra el reporte por pantalla
    public void mostrar() throws SQLException {
        System.out.println("--- " + titulo() + " ---");
        System.out.println(String.join(" | ", encabezados()));
        for (String[] fila : filas()) {
            System.out.println(String.join(" | ", limpiar(fila)));
        }
    }

    // Guarda el reporte en un archivo CSV dentro de la carpeta "exportaciones".
    // Devuelve la ruta completa del archivo creado.
    public String exportarCSV(String nombreArchivo) throws IOException, SQLException {
        Path carpeta = Paths.get("exportaciones");
        Files.createDirectories(carpeta);
        Path archivo = carpeta.resolve(nombreArchivo);

        // try-with-resources: el archivo se cierra solo al terminar
        try (BufferedWriter escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            escritor.write('﻿'); // marca que permite a Excel leer bien los acentos
            escritor.write(String.join(";", encabezados()));
            escritor.newLine();
            for (String[] fila : filas()) {
                escritor.write(String.join(";", limpiar(fila)));
                escritor.newLine();
            }
        }
        return archivo.toAbsolutePath().toString();
    }

    // Saca los ; y los saltos de línea de los textos para no romper el formato del CSV
    private String[] limpiar(String[] fila) {
        String[] limpia = new String[fila.length];
        for (int i = 0; i < fila.length; i++) {
            String texto = fila[i];
            if (texto == null) {
                texto = "";
            }
            limpia[i] = texto.replace(';', ',').replace('\n', ' ').replace('\r', ' ');
        }
        return limpia;
    }
}
