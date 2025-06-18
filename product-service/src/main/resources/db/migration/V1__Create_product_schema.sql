-- V1__Create_product_schema.sql

-- Secuencias
CREATE SEQUENCE IF NOT EXISTS sizes_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS colors_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS categories_id_seq START WITH 1 INCREMENT BY 1;

-- Tablas (sintaxis H2 compatible)
CREATE TABLE IF NOT EXISTS sizes (
                                     id BIGINT DEFAULT nextval('sizes_id_seq') PRIMARY KEY,  -- DEFAULT debe ir antes de PRIMARY KEY
                                     name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS colors (
                                      id BIGINT DEFAULT nextval('colors_id_seq') PRIMARY KEY,
                                      name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT DEFAULT nextval('categories_id_seq') PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL
);

-- Reinicio de secuencias (solo para tests)
ALTER SEQUENCE sizes_id_seq RESTART WITH 5;
ALTER SEQUENCE colors_id_seq RESTART WITH 7;
ALTER SEQUENCE categories_id_seq RESTART WITH 3;

-- Tabla de productos
CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        size_id BIGINT NOT NULL REFERENCES sizes(id) ON DELETE RESTRICT,
                                        color_id BIGINT NOT NULL REFERENCES colors(id) ON DELETE RESTRICT,
                                        category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
                                        description TEXT,
                                        price DOUBLE PRECISION NOT NULL CHECK (price >= 0),
                                        stock INTEGER NOT NULL CHECK (stock >= 0),
                                        image_urls TEXT NOT NULL DEFAULT '[]'
);

-- Índices opcionales para acelerar búsquedas
CREATE INDEX IF NOT EXISTS idx_products_size_id     ON products(size_id);
CREATE INDEX IF NOT EXISTS idx_products_color_id    ON products(color_id);
CREATE INDEX IF NOT EXISTS idx_products_category_id ON products(category_id);