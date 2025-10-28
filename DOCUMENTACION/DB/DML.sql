DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'userProyecto2') THEN
    CREATE ROLE "userProyecto2" LOGIN PASSWORD 'proy123';
  END IF;
END$$;

-- 1)Si ya existe la BD, desconectar sesiones y eliminarla
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'ecommerceDb';

DROP DATABASE IF EXISTS "ecommerceDb";

-- 2) Crear base de datos
CREATE DATABASE "ecommerceDb"
  WITH TEMPLATE = template1
       ENCODING 'UTF8';

-- 3) Conectarse a la nueva base
\connect "ecommerceDb"

-- 4) Crear esquema y dejarlo en el search_path
DROP SCHEMA IF EXISTS ecommerce CASCADE;
CREATE SCHEMA ecommerce;

SET search_path TO ecommerce;

CREATE TABLE roles (
  id          SERIAL PRIMARY KEY,
  codigo      VARCHAR(20) NOT NULL UNIQUE,   -- 'COMUN','MODERADOR','LOGISTICA','ADMIN'
  descripcion VARCHAR(120) NOT NULL
);

CREATE TABLE categorias (
  id          SERIAL PRIMARY KEY,
  nombre      VARCHAR(40) NOT NULL UNIQUE     -- 'Tecnología','Hogar','Académico','Personal','Decoración','Otro'
);


CREATE TABLE estados_moderacion_producto (
  id          SERIAL PRIMARY KEY,
  codigo      VARCHAR(20) NOT NULL UNIQUE,    -- 'PENDIENTE','APROBADO','RECHAZADO'
  descripcion VARCHAR(120) NOT NULL
);

CREATE TABLE estados_pedido (
  id          SERIAL PRIMARY KEY,
  codigo      VARCHAR(20) NOT NULL UNIQUE,    -- 'EN_CURSO','ENTREGADO'
  descripcion VARCHAR(120) NOT NULL
);

CREATE TABLE usuarios (
  id             SERIAL PRIMARY KEY,
  nombre         VARCHAR(120) NOT NULL,
  correo         VARCHAR(120) NOT NULL UNIQUE,
  telefono       VARCHAR(30),
  hash_password  TEXT NOT NULL,
  rol_id         INT NOT NULL REFERENCES roles(id),
  esta_activo    BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_registro TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE sanciones (
  id            SERIAL PRIMARY KEY,
  usuario_id    INT NOT NULL REFERENCES usuarios(id),  
  moderador_id  INT NOT NULL REFERENCES usuarios(id),
  motivo        TEXT NOT NULL,
  fecha_inicio  DATE NOT NULL DEFAULT CURRENT_DATE,
  fecha_fin     DATE,                                   
  estado        VARCHAR(20) NOT NULL,                  
  creado_en     TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT ck_sancion_estado CHECK (estado IN ('ACTIVA','LEVANTADA'))
);

CREATE TABLE productos (
  id                  SERIAL PRIMARY KEY,
  vendedor_id         INT NOT NULL REFERENCES usuarios(id),
  nombre              VARCHAR(140) NOT NULL,
  descripcion         TEXT NOT NULL,
  precio              INT NOT NULL CHECK (precio >= 0),     
  stock               INT NOT NULL CHECK (stock >= 0),
  estado_articulo     VARCHAR(10) NOT NULL CHECK (estado_articulo IN ('NUEVO','USADO')),
  categoria_id        INT NOT NULL REFERENCES categorias(id),
  estado_mod_id       INT NOT NULL REFERENCES estados_moderacion_producto(id),
  ultima_revision_en  TIMESTAMP,
  creado_en           TIMESTAMP NOT NULL DEFAULT NOW(),
  actualizado_en      TIMESTAMP NOT NULL DEFAULT NOW(),
  ultimo_moderador_id INT REFERENCES usuarios(id),
  comentario_rechazo TEXT
);

CREATE TABLE producto_imagenes (
  id           SERIAL PRIMARY KEY,
  producto_id  INT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
  url          TEXT NOT NULL,
  creado_en    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE moderaciones_producto (
  id            SERIAL PRIMARY KEY,
  producto_id   INT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
  moderador_id  INT NOT NULL REFERENCES usuarios(id),
  accion        VARCHAR(20) NOT NULL,     -- 'APROBAR','RECHAZAR','ENVIAR_REVISION'
  motivo        TEXT,
  creado_en     TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT ck_accion_mod CHECK (accion IN ('APROBAR','RECHAZAR','ENVIAR_REVISION'))
);

CREATE TABLE carritos (
  id             SERIAL PRIMARY KEY,
  usuario_id     INT NOT NULL REFERENCES usuarios(id),
  esta_vigente   BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en      TIMESTAMP NOT NULL DEFAULT NOW(),
  actualizado_en TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE carrito_items (
  carrito_id    INT NOT NULL REFERENCES carritos(id) ON DELETE CASCADE,
  producto_id   INT NOT NULL REFERENCES productos(id),
  cantidad      INT NOT NULL CHECK (cantidad >= 0),
  precio        INT NOT NULL CHECK (precio >= 0), 
  creado_en     TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (carrito_id, producto_id)
);

CREATE TABLE tarjetas_guardadas (
  id               SERIAL PRIMARY KEY,
  usuario_id       INT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
  token_pasarela   VARCHAR(140) NOT NULL,
  ultimos4         CHAR(4) NOT NULL,
  marca            VARCHAR(20) NOT NULL,   -- VISA/MC/AMEX
  expiracion_mes   SMALLINT NOT NULL CHECK (expiracion_mes BETWEEN 1 AND 12),
  expiracion_anio  SMALLINT NOT NULL CHECK (expiracion_anio BETWEEN 2024 AND 2100),
  titular          VARCHAR(120) NOT NULL,
  creado_en        TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE (usuario_id, token_pasarela)
);

CREATE TABLE pedidos (
  id                      SERIAL PRIMARY KEY,
  comprador_id            INT NOT NULL REFERENCES usuarios(id),
  total_bruto             INT NOT NULL CHECK (total_bruto >= 0), 
  total_comision          INT NOT NULL CHECK (total_comision >= 0),   
  total_neto_vendedores   INT NOT NULL CHECK (total_neto_vendedores >= 0), 
  estado_pedido_id        INT NOT NULL REFERENCES estados_pedido(id),
  fecha_creacion          TIMESTAMP NOT NULL DEFAULT NOW(),
  fecha_estimada_entrega  DATE,
  fecha_entregado         DATE
);

CREATE TABLE pedido_items (
  pedido_id             INT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
  producto_id           INT NOT NULL REFERENCES productos(id),
  vendedor_id           INT NOT NULL REFERENCES usuarios(id),
  cantidad              INT NOT NULL CHECK (cantidad >= 0),
  precio_unitario       INT NOT NULL CHECK (precio_unitario >= 0),  
  subtotal              INT NOT NULL CHECK (subtotal >= 0), 
  comision              INT NOT NULL CHECK (comision >= 0), 
  neto_vendedor         INT NOT NULL CHECK (neto_vendedor >= 0),
  debe_entregarse_el    DATE,        
  entregado_el          DATE,
  PRIMARY KEY (pedido_id, producto_id)
);

CREATE TABLE envios (
  id               SERIAL PRIMARY KEY,
  pedido_id        INT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
  estado           VARCHAR(20) NOT NULL,    
  fecha_programada DATE NOT NULL,
  actualizado_por  INT REFERENCES usuarios(id),
  actualizado_en   TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT ck_envio_estado CHECK (estado IN ('EN_CURSO','ENTREGADO'))
);

CREATE TABLE pagos (
  id                  SERIAL PRIMARY KEY,
  pedido_id           INT NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
  usuario_id          INT NOT NULL REFERENCES usuarios(id),
  monto               INT NOT NULL CHECK (monto >= 0), -- Q
  metodo              VARCHAR(20) NOT NULL,         -- 'TARJETA'
  tarjeta_guardada_id INT REFERENCES tarjetas_guardadas(id),
  referencia_pasarela TEXT,
  estado              VARCHAR(20) NOT NULL,         
  creado_en           TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT ck_pago_metodo CHECK (metodo IN ('TARJETA')),
  CONSTRAINT ck_pago_estado CHECK (estado IN ('APROBADO','RECHAZADO','PENDIENTE'))
);

CREATE TABLE producto_reviews (
  id            SERIAL PRIMARY KEY,
  producto_id   INT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
  usuario_id    INT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
  pedido_id     INT NULL REFERENCES pedidos(id) ON DELETE SET NULL, 
  rating        SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comentario    TEXT,
  creado_en     TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE (producto_id, usuario_id)   
);

CREATE TABLE notificaciones (
  id          SERIAL PRIMARY KEY,
  usuario_id  INT NOT NULL REFERENCES usuarios(id),
  tipo        VARCHAR(40) NOT NULL,     
  asunto      VARCHAR(160) NOT NULL,
  cuerpo      TEXT NOT NULL,
  --metadata    JSONB,
  enviado     BOOLEAN NOT NULL DEFAULT FALSE,
  enviado_en  TIMESTAMP,
  creado_en   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 5) Permisos para el usuario "userProyecto2"

-- Acceso a la base de datos
GRANT CONNECT ON DATABASE "ecommerceDb" TO "userProyecto2";

-- Acceso al esquema
GRANT USAGE, CREATE ON SCHEMA ecommerce TO "userProyecto2";

-- Acceso a objetos existentes
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA ecommerce TO "userProyecto2";
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ecommerce TO "userProyecto2";
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA ecommerce TO "userProyecto2";

-- Privilegios 
ALTER DEFAULT PRIVILEGES IN SCHEMA ecommerce
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO "userProyecto2";

ALTER DEFAULT PRIVILEGES IN SCHEMA ecommerce
  GRANT ALL ON SEQUENCES TO "userProyecto2";

ALTER DEFAULT PRIVILEGES IN SCHEMA ecommerce
  GRANT EXECUTE ON FUNCTIONS TO "userProyecto2";

ALTER ROLE "userProyecto2" IN DATABASE "ecommerceDb" SET search_path = ecommerce, public;


