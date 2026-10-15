package ar.edu.bitacora;

import ar.edu.bitacora.modelo.Docente;
import ar.edu.bitacora.modelo.Estudiante;
import ar.edu.bitacora.modelo.Persona;
import ar.edu.bitacora.modelo.Tutor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Demostración de polimorfismo: una lista de Persona con objetos de distinto tipo.
public class PruebaPolimorfismo {

    public static void main(String[] args) {

        // La lista es de tipo Persona (la clase general),
        // pero adentro guarda objetos de las clases hijas.
        List<Persona> personas = new ArrayList<>();

        personas.add(new Estudiante(1, "ALU-001", "Jorge", "Gentili", "40111222",
                "jorge.gentili@bitacora.edu", LocalDate.of(2009, 5, 15), true));
        personas.add(new Docente(1, "DOC-001", "Guido", "Gentili", "10549119",
                "guido.gentili@bitacora.edu", true));
        personas.add(new Tutor(1, "Ana", "Laccetti", "25333444",
                "ana.laccetti@bitacora.edu", "1155550000", true));

        // Un solo bucle for-each recorre los tres tipos.
        // p.getRol() ejecuta la versión de la clase real de cada objeto.
        System.out.println("Listado de personas del sistema:");
        for (Persona p : personas) {
            System.out.println("- " + p.getRol() + ": " + p);
        }

        // instanceof sirve cuando necesitás algo propio de una hija.
        System.out.println("\nTeléfonos de tutores:");
        for (Persona p : personas) {
            if (p instanceof Tutor) {
                Tutor t = (Tutor) p;
                System.out.println(t.getApellido() + ": " + t.getTelefono());
            }
        }

        // Esta línea NO compila, porque Persona es abstracta:
        // Persona p = new Persona("X", "Y", "1", "x@y.com");
    }
}