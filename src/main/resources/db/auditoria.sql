-- ============================================================
--  SCHEMA DE AUDITORÍA — SigconBackend
--  Ejecutar una sola vez antes de iniciar la aplicación.
-- ============================================================

CREATE SCHEMA IF NOT EXISTS auditoria;

-- ============================================================
-- Tabla: auditoria.evento
-- Registra cada operación (INSERT / UPDATE / DELETE) realizada
-- sobre cualquier entidad del sistema.
-- ============================================================
CREATE TABLE IF NOT EXISTS auditoria.evento (
    id_evento          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Identificación del registro afectado
    nombre_esquema     VARCHAR(100)  NOT NULL,
    nombre_tabla       VARCHAR(100)  NOT NULL,
    id_registro        VARCHAR(150)  NOT NULL,

    -- Identificación del responsable
    id_usuario         BIGINT        NOT NULL,

    -- Información de la operación
    modulo             VARCHAR(100),
    operacion          VARCHAR(30)   NOT NULL,
    fecha_evento       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),

    -- Información de conexión
    ip_origen          INET,

    -- Información HTTP
    metodo_http        VARCHAR(10),
    endpoint           VARCHAR(500),
    codigo_http        INTEGER,

    -- Resultado
    resultado          VARCHAR(30)   NOT NULL,
    motivo             VARCHAR(500),

    -- Correlación
    id_interaccion     UUID,

    -- Identificación de la aplicación
    version_aplicacion VARCHAR(50),

    CONSTRAINT chk_resultado
        CHECK (resultado IN ('SUCCESS', 'FAILURE'))
);

-- ============================================================
-- Tabla: auditoria.cambio
-- Conserva el detalle de los campos modificados por evento.
-- ============================================================
CREATE TABLE IF NOT EXISTS auditoria.cambio (
    id_cambio      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_evento      BIGINT       NOT NULL REFERENCES auditoria.evento(id_evento),
    nombre_campo   VARCHAR(150) NOT NULL,
    valor_anterior TEXT,
    valor_nuevo    TEXT
);

-- ============================================================
-- Índices para consultas de auditoría
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_evento_tabla_registro
    ON auditoria.evento (nombre_esquema, nombre_tabla, id_registro, fecha_evento DESC);

CREATE INDEX IF NOT EXISTS idx_evento_usuario
    ON auditoria.evento (id_usuario, fecha_evento DESC);

CREATE INDEX IF NOT EXISTS idx_evento_ip
    ON auditoria.evento (ip_origen);

CREATE INDEX IF NOT EXISTS idx_evento_interaccion
    ON auditoria.evento (id_interaccion);

CREATE INDEX IF NOT EXISTS idx_cambio_evento
    ON auditoria.cambio (id_evento);
