CREATE DATABASE IF NOT EXISTS db;

USE db;

-- Tabelle für Komponentenlagerbestand (wird zuerst erstellt, da andere Tabellen darauf verweisen)
CREATE TABLE component_stock (
    component_id INT NOT NULL AUTO_INCREMENT,
    component_name VARCHAR(255) NOT NULL,
    component_quantity INT NOT NULL,
    PRIMARY KEY (component_id)
) ENGINE = InnoDB;

-- Tabelle für Lieferanteninformationen (muss erstellt werden, bevor sie in purchasing_order referenziert wird)
CREATE TABLE vendor (
    vendor_id INT NOT NULL AUTO_INCREMENT,
    vendor_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (vendor_id)
) ENGINE = InnoDB;

-- Tabelle für Lagerbestand der Produkte (muss vor customer_order erstellt werden, da sie dort referenziert wird)
CREATE TABLE product_stock (
    product_id INT NOT NULL AUTO_INCREMENT,
    component_id INT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_mass INT NOT NULL,
    product_quantity INT NOT NULL,
    place_id INT NOT NULL,
    PRIMARY KEY (product_id),
    FOREIGN KEY (component_id) REFERENCES component_stock(component_id)
) ENGINE = InnoDB;

-- Tabelle für Kundenbestellungen (kann jetzt erstellt werden, da product_stock existiert)
CREATE TABLE customer_order (
    co_id INT NOT NULL AUTO_INCREMENT,
    product_id INT NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone_number VARCHAR(255) NOT NULL,
    ordered_quantity INT NOT NULL,
    PRIMARY KEY (co_id),
    FOREIGN KEY (product_id) REFERENCES product_stock(product_id)
) ENGINE = InnoDB;

-- Tabelle für den Status von Kundenbestellungen
CREATE TABLE customer_order_status (
    co_status_id INT NOT NULL AUTO_INCREMENT,
    co_id INT NOT NULL,
    order_status VARCHAR(255) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (co_status_id),
    FOREIGN KEY (co_id) REFERENCES customer_order(co_id)
) ENGINE = InnoDB;

-- Tabelle für Produkttransaktionen
CREATE TABLE product_transaction (
    pt_id INT NOT NULL AUTO_INCREMENT,
    product_id INT NOT NULL,
    transaction_type ENUM('sale', 'restock', 'return', 'adjustment') NOT NULL,
    quantity INT NOT NULL,
    transaction_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (pt_id),
    FOREIGN KEY (product_id) REFERENCES product_stock(product_id)
) ENGINE = InnoDB;

-- Tabelle für Produktionsaufträge
CREATE TABLE production_order (
    po_id INT NOT NULL AUTO_INCREMENT,
    co_id INT NOT NULL,
    product_id INT NOT NULL,
    production_quantity INT NOT NULL,
    ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (po_id),
    FOREIGN KEY (co_id) REFERENCES customer_order(co_id),
    FOREIGN KEY (product_id) REFERENCES product_stock(product_id)
) ENGINE = InnoDB;

-- Tabelle für Einkaufsaufträge
CREATE TABLE purchasing_order (
    purchasing_id INT NOT NULL AUTO_INCREMENT,
    component_id INT NOT NULL,
    vendor_id INT NOT NULL,
    price DOUBLE NOT NULL,
    purchasing_quantity INT NOT NULL,
    approved VARCHAR(255) NOT NULL,
    PRIMARY KEY (purchasing_id),
    FOREIGN KEY (component_id) REFERENCES component_stock(component_id),
    FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id)
) ENGINE = InnoDB;

GRANT ALL PRIVILEGES ON db.* TO 'dev'@'%' IDENTIFIED BY 'dev';
FLUSH PRIVILEGES;

-- Beispiel-Daten für Komponentenlager
INSERT INTO component_stock (component_name, component_quantity) 
VALUES 
('Mountain bike frame', 50),
('Hybrid bicycle wheels', 50),
('Electric bicycle frame', 50);

-- Beispiel-Daten für Lagerbestand von Produkten
INSERT INTO product_stock (component_id, product_name, product_mass, product_quantity, place_id) 
VALUES 
(1, 'Mountain Bike', 10, 50, 1), 
(2, 'Hybrid 40000 Bicycle', 15, 50, 3), 
(3, 'Speed Thriller Electric 147 Bicycle', 20, 50, 5);

-- Beispiel-Daten für Lieferanten
INSERT INTO vendor (vendor_name)
VALUES 
('MountainBikeComponentsShop'),
('HybridComponentsShop'),
('ElectricBikeFactory'),
('Seats4You');
