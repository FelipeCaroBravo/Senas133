DROP TABLE IF EXISTS mensajes_chat_emergencia CASCADE;
DROP TABLE IF EXISTS solicitudes_interprete CASCADE;
DROP TABLE IF EXISTS sms_contingencia CASCADE;
DROP TABLE IF EXISTS respuestas_contexto_emergencia CASCADE;
DROP TABLE IF EXISTS ubicaciones_emergencia CASCADE;
DROP TABLE IF EXISTS emergencias CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre_completo VARCHAR(120) NOT NULL,
    rut VARCHAR(20) NOT NULL UNIQUE,
    numero_documento VARCHAR(50) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    pin_hash VARCHAR(255) NOT NULL,
    verificado BOOLEAN NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE emergencias (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    subtipo VARCHAR(50),
    estado VARCHAR(30) NOT NULL,
    canal VARCHAR(40) NOT NULL DEFAULT 'INTERNET',
    mensaje VARCHAR(250),
    frase_codigo VARCHAR(80),
    modo_camuflaje BOOLEAN NOT NULL DEFAULT FALSE,
    requiere_interprete BOOLEAN NOT NULL DEFAULT FALSE,
    codigo_externo VARCHAR(120),
    latitud DOUBLE PRECISION,
    longitud DOUBLE PRECISION,
    precision_metros DOUBLE PRECISION,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelado_en TIMESTAMP,
    cerrado_en TIMESTAMP,

    CONSTRAINT fk_emergencias_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);

CREATE TABLE ubicaciones_emergencia (
    id BIGSERIAL PRIMARY KEY,
    emergencia_id BIGINT NOT NULL,
    latitud DOUBLE PRECISION NOT NULL,
    longitud DOUBLE PRECISION NOT NULL,
    precision_metros DOUBLE PRECISION,
    fuente VARCHAR(40),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ubicaciones_emergencia
        FOREIGN KEY (emergencia_id)
        REFERENCES emergencias(id)
        ON DELETE CASCADE
);

CREATE TABLE respuestas_contexto_emergencia (
    id BIGSERIAL PRIMARY KEY,
    emergencia_id BIGINT NOT NULL,
    codigo_pregunta VARCHAR(60) NOT NULL,
    texto_pregunta VARCHAR(180) NOT NULL,
    respuesta BOOLEAN NOT NULL,
    orden INTEGER NOT NULL,
    modo_camuflaje BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_respuestas_contexto_emergencia
        FOREIGN KEY (emergencia_id)
        REFERENCES emergencias(id)
        ON DELETE CASCADE
);

CREATE TABLE solicitudes_interprete (
    id BIGSERIAL PRIMARY KEY,
    emergencia_id BIGINT NOT NULL UNIQUE,
    estado VARCHAR(30) NOT NULL,
    nombre_interprete VARCHAR(120),
    url_sala_video VARCHAR(300),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_solicitudes_interprete_emergencia
        FOREIGN KEY (emergencia_id)
        REFERENCES emergencias(id)
        ON DELETE CASCADE
);

CREATE TABLE mensajes_chat_emergencia (
    id BIGSERIAL PRIMARY KEY,
    emergencia_id BIGINT NOT NULL,
    rol_emisor VARCHAR(30) NOT NULL,
    mensaje VARCHAR(1000) NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_mensajes_chat_emergencia
        FOREIGN KEY (emergencia_id)
        REFERENCES emergencias(id)
        ON DELETE CASCADE
);

CREATE TABLE sms_contingencia (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    emergencia_id BIGINT UNIQUE,
    tipo VARCHAR(40) NOT NULL,
    subtipo VARCHAR(50),
    estado VARCHAR(30) NOT NULL,
    numero_destino VARCHAR(30) NOT NULL,
    texto_sms VARCHAR(500) NOT NULL,
    latitud DOUBLE PRECISION,
    longitud DOUBLE PRECISION,
    precision_metros DOUBLE PRECISION,
    enviado_por_usuario BOOLEAN NOT NULL DEFAULT FALSE,
    sincronizado BOOLEAN NOT NULL DEFAULT FALSE,
    codigo_dispositivo VARCHAR(120),
    generado_en_dispositivo TIMESTAMP,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_sms_contingencia_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_sms_contingencia_emergencia
        FOREIGN KEY (emergencia_id)
        REFERENCES emergencias(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_emergencias_estado ON emergencias(estado);
CREATE INDEX idx_emergencias_usuario_id ON emergencias(usuario_id);
CREATE INDEX idx_emergencias_modo_camuflaje ON emergencias(modo_camuflaje);
CREATE INDEX idx_ubicaciones_emergencia_id ON ubicaciones_emergencia(emergencia_id);
CREATE INDEX idx_respuestas_contexto_emergencia_id ON respuestas_contexto_emergencia(emergencia_id);
CREATE INDEX idx_mensajes_chat_emergencia_id ON mensajes_chat_emergencia(emergencia_id);
CREATE INDEX idx_sms_contingencia_usuario_id ON sms_contingencia(usuario_id);

-- PIN de prueba para ambos usuarios: 1234
INSERT INTO usuarios (
    nombre_completo,
    rut,
    numero_documento,
    telefono,
    pin_hash,
    verificado
) VALUES (
    'Juan Carlos Pérez Muñoz',
    '12345678-9',
    '987654321',
    '+56912345678',
    '$2y$10$KujWmyqhWWGEepaGVB7gG.i/MtiMSEWFKMb5bHeItMvkB9T4n3we.',
    true
);

INSERT INTO usuarios (
    nombre_completo,
    rut,
    numero_documento,
    telefono,
    pin_hash,
    verificado
) VALUES (
    'María González Soto',
    '11111111-1',
    '111222333',
    '+56987654321',
    '$2y$10$KujWmyqhWWGEepaGVB7gG.i/MtiMSEWFKMb5bHeItMvkB9T4n3we.',
    true
);

INSERT INTO emergencias (
    usuario_id,
    tipo,
    subtipo,
    estado,
    canal,
    mensaje,
    frase_codigo,
    modo_camuflaje,
    requiere_interprete,
    latitud,
    longitud,
    precision_metros
) VALUES (
    1,
    'DELITOS_GRAVES',
    'ROBO',
    'ENVIADA',
    'INTERNET',
    'Robo con violencia',
    'FRASE_ROBO',
    false,
    true,
    -36.82699,
    -73.04977,
    5
);

INSERT INTO ubicaciones_emergencia (
    emergencia_id,
    latitud,
    longitud,
    precision_metros,
    fuente
) VALUES (
    1,
    -36.82699,
    -73.04977,
    5,
    'GPS'
);

INSERT INTO solicitudes_interprete (
    emergencia_id,
    estado,
    nombre_interprete,
    url_sala_video
) VALUES (
    1,
    'ASIGNADO',
    'Intérprete LSCh Demo',
    'https://meet.demo/senas133-emergencia-1'
);

INSERT INTO emergencias (
    usuario_id,
    tipo,
    subtipo,
    estado,
    canal,
    mensaje,
    frase_codigo,
    modo_camuflaje,
    requiere_interprete,
    latitud,
    longitud,
    precision_metros
) VALUES (
    2,
    'DELITOS_GRAVES',
    'VIOLENCIA_INTRAFAMILIAR',
    'ENVIADA',
    'INTERNET',
    'Alerta iniciada desde modo camuflaje',
    'FRASE_VIF',
    true,
    true,
    -36.82010,
    -73.04450,
    10
);

INSERT INTO respuestas_contexto_emergencia (
    emergencia_id,
    codigo_pregunta,
    texto_pregunta,
    respuesta,
    orden,
    modo_camuflaje
) VALUES
    (2, 'AGRESOR_ARMADO', 'El agresor está armado', true, 1, true),
    (2, 'AGRESOR_PRESENTE', 'El agresor sigue presente', true, 2, true),
    (2, 'VICTIMA_HERIDA', 'La víctima está herida', false, 3, true);

INSERT INTO solicitudes_interprete (
    emergencia_id,
    estado
) VALUES (
    2,
    'SOLICITADO'
);

INSERT INTO mensajes_chat_emergencia (
    emergencia_id,
    rol_emisor,
    mensaje
) VALUES
    (1, 'SISTEMA', 'Alerta recibida por CENCO.'),
    (1, 'OPERADOR_CENCO', 'Patrulla en camino. Manténgase en un lugar seguro si es posible.');
