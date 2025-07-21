DROP TABLE IF EXISTS shipments;

CREATE TABLE IF NOT EXISTS shipments (
    shipId INT PRIMARY KEY,
    productCode INT,
    orderDate DATETIME,
    address VARCHAR(255),
    status VARCHAR(255)
);