CREATE TABLE capacidad (
                           id BIGSERIAL PRIMARY KEY,
                           nombre VARCHAR(255) NOT NULL UNIQUE,
                           descripcion TEXT,
                           tecnologias TEXT
);
