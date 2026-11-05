-- BITÁCORA - AP4: preparación de datos para probar el módulo académico.
-- Se ejecuta una sola vez, desde MySQL, con un usuario que pueda escribir (ej: root).
USE bitacora_db;

-- Paso 1: mirar en qué curso se dicta el dictado 3 (Matemática)
SELECT d.id_dictado, d.id_curso FROM dictados d;

-- Paso 2: inscribir a Jorge (ALU-001) y Eitel (ALU-002) en ese curso, solo si todavía no lo están.
-- Se puede ejecutar más de una vez sin duplicar nada.
INSERT INTO inscripciones (id_estudiante, id_curso, fecha_inscripcion, estado)
SELECT e.id_estudiante, d.id_curso, CURRENT_DATE(), 'ACTIVA'
FROM estudiantes e
JOIN dictados d ON d.id_dictado = 3
WHERE e.legajo IN ('ALU-001', 'ALU-002')
  AND NOT EXISTS (SELECT 1 FROM inscripciones i
                  WHERE i.id_estudiante = e.id_estudiante AND i.id_curso = d.id_curso);

-- Paso 3: verificar el resultado
SELECT i.id_inscripcion, e.legajo, e.apellido, c.nombre AS curso, c.division, i.estado
FROM inscripciones i
JOIN estudiantes e ON e.id_estudiante = i.id_estudiante
JOIN cursos c ON c.id_curso = i.id_curso
ORDER BY i.id_inscripcion;
