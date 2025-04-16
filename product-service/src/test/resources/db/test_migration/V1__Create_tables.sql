CREATE TABLE IF NOT EXISTS sizes (
                                     id BIGINT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS colors (
                                      id BIGINT PRIMARY KEY,
                                      name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT PRIMARY KEY,
                                          name VARCHAR(50) NOT NULL
);