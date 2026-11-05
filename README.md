# Bitácora – Sistema Integral de Gestión Educativa Académica

Proyecto de la materia **Seminario de Práctica de Informática** (versión 2, entrega AP4).
Autor: Jorge Gentili.

Prototipo en **Java 17**, **Maven** y **MySQL 8** (JDBC) que se usa desde la consola.

## Qué incluye

- **Módulo de personas (AP3):** alta, listado, búsqueda y baja de estudiantes; listado de estudiantes y docentes con polimorfismo (`MenuBitacora`).
- **Módulo académico (AP4):** inscripciones, evaluaciones, calificaciones con estadísticas, asistencia, boletín, exportación e importación de CSV (`MenuAcademico`).

## Patrón de diseño

- **DAO:** interfaz `Dao<T>` + clase abstracta `BaseDAO<T>` + un DAO por tabla (paquete `dao`).
- **Template Method:** clase abstracta `Reporte` con `ReporteCalificaciones` y `ReporteAsistencia`.

## Estructura (`src/main/java/ar/edu/bitacora`)

| Paquete | Contenido |
|---|---|
| `config` | `ConexionMySQL` (lee `db.properties`) |
| `modelo` | Clases del dominio (`Persona`, `Estudiante`, `Calificacion`, `Evaluacion`, `Inscripcion`, `Asistencia`, ...) |
| `dao` | `Dao`, `BaseDAO` y los DAO |
| `excepciones` | `DatoDuplicadoException`, `ReferenciaInvalidaException`, `NotaInvalidaException` |
| `util` | `Estadistica` (arreglos) |
| `reportes` | `Reporte` y reportes concretos |
| `archivos` | `ImportadorCalificaciones` |

## Cómo ejecutarlo

1. Crear la base ejecutando `database/database_bitacora.sql` en MySQL.
2. Cargar datos de prueba del módulo académico: `database/ap4_datos_prueba.sql`.
3. Copiar `db.properties.example` como `db.properties` y completar usuario y contraseña.
4. Ejecutar:

```
mvn compile exec:java -Dexec.mainClass=ar.edu.bitacora.MenuAcademico
mvn compile exec:java -Dexec.mainClass=ar.edu.bitacora.MenuBitacora
mvn compile exec:java -Dexec.mainClass=ar.edu.bitacora.PruebaArreglosArrayList
```

Los reportes exportados se guardan en `exportaciones/`. El archivo de ejemplo para importar notas está en `datos/calificaciones_ejemplo.csv` (formato `legajo;nota;observacion`).

## Pendientes

- Que `EstudianteDAO`, `DocenteDAO` y `CursoDAO` implementen `Dao<T>`.
- Unir los dos menús en un menú principal.
- DAO de Tutor y Familia.
