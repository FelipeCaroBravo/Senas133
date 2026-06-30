CREATE DATABASE IF NOT EXISTS mediacion_directa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mediacion_directa;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS mensajes_chat_emergencia;
DROP TABLE IF EXISTS respuestas_contexto_emergencia;
DROP TABLE IF EXISTS sms_contingencia;
DROP TABLE IF EXISTS solicitudes_interprete;
DROP TABLE IF EXISTS ubicaciones_emergencia;
DROP TABLE IF EXISTS emergencias;
DROP TABLE IF EXISTS usuarios;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre_completo VARCHAR(120) NOT NULL,
    rut VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(50),
    direccion_principal VARCHAR(180),
    telefono VARCHAR(30),
    pin_hash VARCHAR(255),
    verificado BIT NOT NULL,
    clave_unica_validada BIT NOT NULL,
    perfil_completo BIT NOT NULL,
    codigo_dispositivo VARCHAR(80),
    creado_en DATETIME(6) NOT NULL,
    actualizado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_rut (rut)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE emergencias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    subtipo VARCHAR(60),
    estado VARCHAR(40) NOT NULL,
    canal VARCHAR(40) NOT NULL,
    mensaje VARCHAR(250),
    frase_codigo VARCHAR(80),
    modo_camuflaje BIT NOT NULL,
    requiere_interprete BIT NOT NULL,
    detalle_posterior_pendiente BIT NOT NULL,
    lugar_seguro_para_mas_informacion BIT,
    codigo_externo VARCHAR(120),
    latitud DOUBLE,
    longitud DOUBLE,
    precision_metros DOUBLE,
    creado_en DATETIME(6) NOT NULL,
    actualizado_en DATETIME(6) NOT NULL,
    recibida_en DATETIME(6),
    patrulla_despachada_en DATETIME(6),
    cancelado_en DATETIME(6),
    cerrado_en DATETIME(6),
    PRIMARY KEY (id),
    KEY idx_emergencias_usuario_id (usuario_id),
    KEY idx_emergencias_estado (estado),
    CONSTRAINT fk_emergencias_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ubicaciones_emergencia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    emergencia_id BIGINT NOT NULL,
    latitud DOUBLE NOT NULL,
    longitud DOUBLE NOT NULL,
    precision_metros DOUBLE,
    fuente VARCHAR(40),
    creado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_ubicaciones_emergencia_id (emergencia_id),
    CONSTRAINT fk_ubicaciones_emergencia FOREIGN KEY (emergencia_id) REFERENCES emergencias(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE solicitudes_interprete (
    id BIGINT NOT NULL AUTO_INCREMENT,
    emergencia_id BIGINT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    nombre_interprete VARCHAR(120),
    url_sala_video VARCHAR(300),
    creado_en DATETIME(6) NOT NULL,
    actualizado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_solicitudes_interprete_emergencia (emergencia_id),
    CONSTRAINT fk_solicitudes_interprete_emergencia FOREIGN KEY (emergencia_id) REFERENCES emergencias(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE respuestas_contexto_emergencia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    emergencia_id BIGINT NOT NULL,
    codigo_pregunta VARCHAR(60) NOT NULL,
    texto_pregunta VARCHAR(180) NOT NULL,
    respuesta BIT NOT NULL,
    orden INT NOT NULL,
    modo_camuflaje BIT NOT NULL,
    creado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_respuestas_emergencia_id (emergencia_id),
    CONSTRAINT fk_respuestas_emergencia FOREIGN KEY (emergencia_id) REFERENCES emergencias(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mensajes_chat_emergencia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    emergencia_id BIGINT NOT NULL,
    rol_emisor VARCHAR(30) NOT NULL,
    mensaje VARCHAR(1000) NOT NULL,
    creado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_mensajes_emergencia_id (emergencia_id),
    CONSTRAINT fk_mensajes_emergencia FOREIGN KEY (emergencia_id) REFERENCES emergencias(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sms_contingencia (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    emergencia_id BIGINT,
    tipo VARCHAR(40) NOT NULL,
    subtipo VARCHAR(60),
    estado VARCHAR(30) NOT NULL,
    numero_destino VARCHAR(30) NOT NULL,
    texto_sms VARCHAR(500) NOT NULL,
    latitud DOUBLE,
    longitud DOUBLE,
    precision_metros DOUBLE,
    enviado_por_usuario BIT NOT NULL,
    sincronizado BIT NOT NULL,
    codigo_dispositivo VARCHAR(120),
    generado_en_dispositivo DATETIME(6),
    creado_en DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_sms_usuario_id (usuario_id),
    UNIQUE KEY uk_sms_emergencia_id (emergencia_id),
    CONSTRAINT fk_sms_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_sms_emergencia FOREIGN KEY (emergencia_id) REFERENCES emergencias(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- PIN de prueba para usuarios completos: 1234
SET @pin1234 = '$2a$10$KujWmyqhWWGEepaGVB7gG.i/MtiMSEWFKMb5bHeItMvkB9T4n3we.';
SET @now = NOW(6);

INSERT INTO usuarios (
    id, nombre_completo, rut, numero_documento, direccion_principal, telefono, pin_hash,
    verificado, clave_unica_validada, perfil_completo, codigo_dispositivo, creado_en, actualizado_en
) VALUES
(1, 'Usuario Demo Principal', '11111111-1', 'DOC-DEMO-001', 'Av. Los Carrera 123, Depto 402', '+56900000001', @pin1234, b'1', b'1', b'1', 'DEVICE-DEMO-001', @now, @now),
(2, 'Usuario Demo Pendiente Perfil', '22222222-2', 'DOC-DEMO-002', NULL, NULL, NULL, b'1', b'1', b'0', 'DEVICE-DEMO-002', @now, @now),
(3, 'Usuario Camuflaje Demo', '33333333-3', 'DOC-DEMO-003', 'Dirección demo protegida 456', '+56900000003', @pin1234, b'1', b'1', b'1', 'DEVICE-DEMO-003', @now, @now);

INSERT INTO emergencias (
    id, usuario_id, tipo, subtipo, estado, canal, mensaje, frase_codigo,
    modo_camuflaje, requiere_interprete, detalle_posterior_pendiente, lugar_seguro_para_mas_informacion,
    codigo_externo, latitud, longitud, precision_metros, creado_en, actualizado_en, recibida_en, patrulla_despachada_en
) VALUES
(1, 1, 'DELITOS_GRAVES', 'ALERTA_SILENCIOSA', 'ENVIADA', 'INTERNET', 'Alerta silenciosa', 'ALERTA_SILENCIOSA', b'0', b'1', b'1', NULL, 'CENCO-DEMO-0001', -36.826990, -73.049770, 5, @now, @now, NULL, NULL),
(2, 1, 'ASISTENCIA_VIAL', 'ACCIDENTE_TRANSITO', 'PATRULLA_DESPACHADA', 'INTERNET', 'Accidente de tránsito', 'FRASE_ACCIDENTE_TRANSITO', b'0', b'0', b'0', NULL, 'CENCO-DEMO-0002', -36.820100, -73.044500, 8, @now, @now, @now, @now),
(3, 3, 'DELITOS_GRAVES', 'VIOLENCIA_INTRAFAMILIAR', 'RECIBIDA', 'INTERNET', 'Alerta camuflada enviada desde puzzle', 'CAMUFLAJE_PUZZLE', b'1', b'1', b'0', b'0', 'CENCO-DEMO-0003', -36.828000, -73.051000, 12, @now, @now, @now, NULL);

INSERT INTO ubicaciones_emergencia (emergencia_id, latitud, longitud, precision_metros, fuente, creado_en) VALUES
(1, -36.826990, -73.049770, 5, 'INICIAL', @now),
(2, -36.820100, -73.044500, 8, 'INICIAL', @now),
(3, -36.828000, -73.051000, 12, 'CAMUFLAJE', @now);

INSERT INTO solicitudes_interprete (emergencia_id, estado, nombre_interprete, url_sala_video, creado_en, actualizado_en) VALUES
(1, 'SOLICITADO', NULL, NULL, @now, @now),
(3, 'SOLICITADO', NULL, NULL, @now, @now);

INSERT INTO respuestas_contexto_emergencia (emergencia_id, codigo_pregunta, texto_pregunta, respuesta, orden, modo_camuflaje, creado_en) VALUES
(3, 'AGRESOR_CERCA', 'El agresor está cerca', b'1', 1, b'1', @now),
(3, 'HAY_ARMA', 'El agresor tiene un arma u objeto peligroso', b'0', 2, b'1', @now),
(3, 'NECESITA_AYUDA_INMEDIATA', 'La víctima necesita ayuda inmediata', b'1', 3, b'1', @now);

INSERT INTO mensajes_chat_emergencia (emergencia_id, rol_emisor, mensaje, creado_en) VALUES
(1, 'SISTEMA', 'Alerta enviada a CENCO simulado.', @now),
(2, 'OPERADOR_CENCO', 'Unidad despachada hacia la ubicación.', @now);

INSERT INTO sms_contingencia (
    usuario_id, emergencia_id, tipo, subtipo, estado, numero_destino, texto_sms,
    latitud, longitud, precision_metros, enviado_por_usuario, sincronizado, codigo_dispositivo,
    generado_en_dispositivo, creado_en
) VALUES
(1, NULL, 'DELITOS_GRAVES', 'ALERTA_SILENCIOSA', 'PREPARADO', '+56900000000', 'SEÑAS133 ALERTA SILENCIOSA ID:1 GPS:-36.82699,-73.04977', -36.82699, -73.04977, 5, b'0', b'0', 'DEVICE-DEMO-001', @now, @now);
