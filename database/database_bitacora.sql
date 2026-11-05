DROP DATABASE IF EXISTS bitacora_db;
CREATE DATABASE bitacora_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bitacora_db;

-- Un curso: por ejemplo "Primer Año, división A, 2026"
CREATE TABLE cursos (
 id_curso INT AUTO_INCREMENT PRIMARY KEY,
 nombre VARCHAR(50) NOT NULL,
 anio_lectivo INT NOT NULL,
 division VARCHAR(10) NOT NULL,
 UNIQUE KEY uk_curso (nombre, anio_lectivo, division)
);

-- Un estudiante
CREATE TABLE estudiantes (
 id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
 legajo VARCHAR(20) NOT NULL UNIQUE,
 nombre VARCHAR(50) NOT NULL,
 apellido VARCHAR(50) NOT NULL,
 dni VARCHAR(15) NOT NULL UNIQUE,
 email VARCHAR(100) UNIQUE,
 fecha_nacimiento DATE,
 activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla intermedia: qué estudiante está en qué curso
CREATE TABLE inscripciones (
 id_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
 id_estudiante INT NOT NULL,
 id_curso INT NOT NULL,
 fecha_inscripcion DATE NOT NULL,
 estado ENUM('ACTIVA','FINALIZADA','CANCELADA') NOT NULL DEFAULT 'ACTIVA',
 UNIQUE KEY uk_inscripcion (id_estudiante,id_curso),
 CONSTRAINT fk_inscripcion_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante),
 CONSTRAINT fk_inscripcion_curso FOREIGN KEY (id_curso) REFERENCES cursos(id_curso)
);

-- Un docente
CREATE TABLE docentes (
 id_docente INT AUTO_INCREMENT PRIMARY KEY,
 legajo VARCHAR(20) NOT NULL UNIQUE,
 nombre VARCHAR(50) NOT NULL,
 apellido VARCHAR(50) NOT NULL,
 dni VARCHAR(15) NOT NULL UNIQUE,
 email VARCHAR(100),
 activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Una materia (Matemática, Historia, etc.)
CREATE TABLE materias (
 id_materia INT AUTO_INCREMENT PRIMARY KEY,
 nombre VARCHAR(80) NOT NULL UNIQUE,
 descripcion VARCHAR(255)
);

-- Qué docente dicta qué materia en qué curso
CREATE TABLE dictados (
 id_dictado INT AUTO_INCREMENT PRIMARY KEY,
 id_docente INT NOT NULL,
 id_materia INT NOT NULL,
 id_curso INT NOT NULL,
 UNIQUE KEY uk_dictado (id_docente,id_materia,id_curso),
 CONSTRAINT fk_dictado_docente FOREIGN KEY (id_docente) REFERENCES docentes(id_docente),
 CONSTRAINT fk_dictado_materia FOREIGN KEY (id_materia) REFERENCES materias(id_materia),
 CONSTRAINT fk_dictado_curso FOREIGN KEY (id_curso) REFERENCES cursos(id_curso)
);

-- Asistencia de un estudiante inscripto, en una fecha puntual
CREATE TABLE asistencias (
 id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
 id_inscripcion INT NOT NULL,
 fecha DATE NOT NULL,
 estado ENUM('PRESENTE','AUSENTE','JUSTIFICADA','TARDANZA') NOT NULL,
 observacion VARCHAR(255),
 UNIQUE KEY uk_asistencia (id_inscripcion,fecha),
 CONSTRAINT fk_asistencia_inscripcion FOREIGN KEY (id_inscripcion) REFERENCES inscripciones(id_inscripcion)
);

-- Una evaluación (examen, trabajo práctico) de una materia dictada
CREATE TABLE evaluaciones (
 id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
 id_dictado INT NOT NULL,
 titulo VARCHAR(100) NOT NULL,
 fecha DATE NOT NULL,
 tipo VARCHAR(30) NOT NULL,
 CONSTRAINT fk_evaluacion_dictado FOREIGN KEY (id_dictado) REFERENCES dictados(id_dictado)
);

-- La nota de un estudiante en una evaluación puntual
CREATE TABLE calificaciones (
 id_calificacion INT AUTO_INCREMENT PRIMARY KEY,
 id_evaluacion INT NOT NULL,
 id_inscripcion INT NOT NULL,
 nota DECIMAL(4,2) NOT NULL,
 observacion VARCHAR(255),
 UNIQUE KEY uk_calificacion (id_evaluacion,id_inscripcion),
 CONSTRAINT fk_calificacion_evaluacion FOREIGN KEY (id_evaluacion) REFERENCES evaluaciones(id_evaluacion),
 CONSTRAINT fk_calificacion_inscripcion FOREIGN KEY (id_inscripcion) REFERENCES inscripciones(id_inscripcion),
 CONSTRAINT chk_nota CHECK (nota BETWEEN 0 AND 10)
);

-- Datos de ejemplo para poder probar el sistema
INSERT INTO cursos(nombre,anio_lectivo,division) VALUES
('Primer Año',2026,'A'),('Segundo Año',2026,'A'),('Tercer Año',2026,'B');

INSERT INTO estudiantes(legajo,nombre,apellido,dni,email,fecha_nacimiento) VALUES
('ALU-001','Juan','Pérez','40111222','juan.perez@bitacora.edu','2009-05-15'),
('ALU-002','Sofía','Gómez','40222333','sofia.gomez@bitacora.edu','2009-08-21');

INSERT INTO inscripciones(id_estudiante,id_curso,fecha_inscripcion) VALUES
(1,1,CURRENT_DATE()),(2,1,CURRENT_DATE());


CREATE USER IF NOT EXISTS 'bitacora_app'@'localhost' IDENTIFIED BY 'CAMBIAR_CLAVE';
GRANT SELECT, INSERT, UPDATE, DELETE ON bitacora_db.* TO 'bitacora_app'@'localhost';
FLUSH PRIVILEGES;