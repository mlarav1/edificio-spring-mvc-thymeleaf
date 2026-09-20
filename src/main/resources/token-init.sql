-- Se ejecuta al arrancar (spring.sql.init) antes de que JPA valide el esquema.
-- Es idempotente: no toca las tablas existentes ni sus datos.
CREATE TABLE IF NOT EXISTS token_recuperacion (
    token_hash  VARCHAR(64) PRIMARY KEY,
    usuario_id  VARCHAR(120) NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    expira      TIMESTAMP    NOT NULL
);
