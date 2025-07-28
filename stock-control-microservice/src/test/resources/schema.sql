DROP TABLE IF EXISTS product;

CREATE TABLE IF NOT EXISTS product (
    productCode INT PRIMARY KEY, -- Clave primaria, sin autoincremento
    productName VARCHAR(255),    -- Usamos 255 como un valor seguro para VARCHAR
    productCategory VARCHAR(255),
    unitaryProductPrice DOUBLE,
    quantityInStock INT
);