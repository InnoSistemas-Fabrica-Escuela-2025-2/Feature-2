-- Script para crear las tablas necesarias en PostgreSQL

-- Crear tabla de equipos
CREATE TABLE IF NOT EXISTS equipos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

-- Crear tabla de proyectos
CREATE TABLE IF NOT EXISTS proyectos (
    id BIGSERIAL PRIMARY KEY,
    nombre_proyecto VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_final TIMESTAMP NOT NULL,
    id_equipo BIGINT NOT NULL,
    FOREIGN KEY (id_equipo) REFERENCES equipos(id)
);

-- Crear tabla de tareas
CREATE TABLE IF NOT EXISTS tareas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_entrega TIMESTAMP NOT NULL,
    id_proyecto BIGINT NOT NULL,
    FOREIGN KEY (id_proyecto) REFERENCES proyectos(id) ON DELETE CASCADE
);

-- Insertar datos de ejemplo
INSERT INTO equipos (nombre) VALUES
('Equipo Alpha'),
('Equipo Beta'),
('Equipo Gamma')
ON CONFLICT DO NOTHING;
