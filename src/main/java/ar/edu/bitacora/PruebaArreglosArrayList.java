package ar.edu.bitacora;

import ar.edu.bitacora.excepciones.NotaInvalidaException;
import ar.edu.bitacora.modelo.Calificacion;
import ar.edu.bitacora.util.Estadistica;
import java.util.ArrayList;

// Prueba que NO usa la base de datos: muestra cómo se usan juntos
// un ArrayList (lista que crece) y un arreglo (tamaño fijo).
public class PruebaArreglosArrayList {

    public static void main(String[] args) {
        // 1) ArrayList: vamos agregando calificaciones sin saber cuántas serán
        ArrayList<Calificacion> lista = new ArrayList<>();
        double[] notasDePrueba = {8.5, 6, 4, 9.5, 7, 10, 5.5};
        try {
            for (int i = 0; i < notasDePrueba.length; i++) {
                lista.add(new Calificacion(1, i + 1, notasDePrueba[i], "prueba"));
            }
            // Esta nota es inválida: el programa la rechaza con una excepción
            lista.add(new Calificacion(1, 99, 11, "fuera de rango"));
        } catch (NotaInvalidaException e) {
            System.out.println("Error controlado: " + e.getMessage());
        }
        System.out.println("Calificaciones guardadas en el ArrayList: " + lista.size());

        // 2) Arreglo: se pasa a un arreglo de tamaño fijo para hacer las cuentas
        double[] notas = Estadistica.aArreglo(lista);
        System.out.println("Longitud del arreglo: " + notas.length);
        System.out.println("Promedio: " + String.format("%.2f", Estadistica.promedio(notas)));
        System.out.println("Máxima: " + Estadistica.maximo(notas));
        System.out.println("Mínima: " + Estadistica.minimo(notas));
        System.out.println("Aprobados: " + Estadistica.contarAprobados(notas) + " de " + notas.length);

        // 3) Ordenamiento de mayor a menor (sobre una copia del arreglo)
        double[] ordenadas = Estadistica.ordenarDeMayorAMenor(notas);
        System.out.print("Ordenadas de mayor a menor:");
        for (double n : ordenadas) {
            System.out.print(" " + n);
        }
        System.out.println();
    }
}
