package ar.edu.bitacora.util;

import ar.edu.bitacora.modelo.Asistencia;
import ar.edu.bitacora.modelo.Calificacion;
import ar.edu.bitacora.modelo.EstadoAsistencia;
import java.util.ArrayList;
import java.util.Arrays;

// Cálculos sobre notas y asistencias.
// Los datos llegan de la base en un ArrayList (que crece solo, no sabemos cuántos son)
// y acá se pasan a un arreglo (tamaño fijo) para hacer las cuentas.
public final class Estadistica {

    public static final double NOTA_APROBACION = 6.0;

    private Estadistica() {
    }

    // Pasa las notas de un ArrayList de Calificacion a un arreglo de double
    public static double[] aArreglo(ArrayList<Calificacion> lista) {
        double[] notas = new double[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            notas[i] = lista.get(i).getNota();
        }
        return notas;
    }

    public static double promedio(double[] notas) {
        if (notas.length == 0) {
            return 0;
        }
        double suma = 0;
        for (double n : notas) {
            suma += n;
        }
        return suma / notas.length;
    }

    public static double maximo(double[] notas) {
        double mayor = notas[0];
        for (int i = 1; i < notas.length; i++) {
            if (notas[i] > mayor) {
                mayor = notas[i];
            }
        }
        return mayor;
    }

    public static double minimo(double[] notas) {
        double menor = notas[0];
        for (int i = 1; i < notas.length; i++) {
            if (notas[i] < menor) {
                menor = notas[i];
            }
        }
        return menor;
    }

    public static int contarAprobados(double[] notas) {
        int aprobados = 0;
        for (double n : notas) {
            if (n >= NOTA_APROBACION) {
                aprobados++;
            }
        }
        return aprobados;
    }

    // Devuelve una COPIA de las notas ordenada de mayor a menor (ordenamiento por selección).
    // Se trabaja con una copia para no cambiar el arreglo original.
    public static double[] ordenarDeMayorAMenor(double[] notas) {
        double[] copia = Arrays.copyOf(notas, notas.length);
        for (int i = 0; i < copia.length - 1; i++) {
            int posMayor = i;
            for (int j = i + 1; j < copia.length; j++) {
                if (copia[j] > copia[posMayor]) {
                    posMayor = j;
                }
            }
            double aux = copia[i];
            copia[i] = copia[posMayor];
            copia[posMayor] = aux;
        }
        return copia;
    }

    // Cuenta cuántas asistencias hay de cada estado.
    // La posición del arreglo coincide con el orden del enum:
    // [0] PRESENTE, [1] AUSENTE, [2] JUSTIFICADA, [3] TARDANZA
    public static int[] contarAsistencias(ArrayList<Asistencia> lista) {
        int[] conteo = new int[EstadoAsistencia.values().length];
        for (Asistencia a : lista) {
            conteo[a.getEstado().ordinal()]++;
        }
        return conteo;
    }

    // Porcentaje de clases a las que vino (se cuentan PRESENTE y TARDANZA como "vino")
    public static double porcentajePresentismo(int[] conteo) {
        int total = 0;
        for (int cantidad : conteo) {
            total += cantidad;
        }
        if (total == 0) {
            return 0;
        }
        int vino = conteo[EstadoAsistencia.PRESENTE.ordinal()]
                 + conteo[EstadoAsistencia.TARDANZA.ordinal()];
        return vino * 100.0 / total;
    }
}
