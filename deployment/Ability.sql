CREATE TABLE capacidad (
                           id BIGSERIAL PRIMARY KEY,
                           nombre VARCHAR(255) NOT NULL,
                           descripcion TEXT,
                           tecnologias TEXT
);


CREATE TABLE tecnologia_capacidad (
    capacidad_id BIGINT NOT NULL,
    tecnologia_id BIGINT NOT NULL,
    PRIMARY KEY (capacidad_id, tecnologia_id)
);
