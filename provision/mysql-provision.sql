CREATE DATABASE finished_product_DB;
CREATE DATABASE production_order_DB;
CREATE DATABASE component_DB;
CREATE DATABASE purchasing_DB;
CREATE DATABASE warehouse;

CREATE TABLE `customer_DB`.`customer_order` 
(
    `id` INT(11) NOT NULL AUTO_INCREMENT , 
    `name` VARCHAR(255) NOT NULL , 
    `email` VARCHAR(255) NOT NULL , 
    `phone` VARCHAR(255) NOT NULL , 
    `address` VARCHAR(255) NOT NULL , 
    `product` VARCHAR(255) NOT NULL , 
    `quantity` INT(11) NOT NULL , 
    `orderStatus` VARCHAR(255) NOT NULL , 
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;


CREATE TABLE `finished_product_DB`.`finished_product_stock` 
(
    `id` INT NOT NULL AUTO_INCREMENT , 
    `productName` VARCHAR(255) NOT NULL , 
    `productQuantity` INT NOT NULL , PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `production_order_DB`.`production_order` 
(
    `productionOrderID` INT(11) NOT NULL AUTO_INCREMENT , 
    `orderID` INT(11) NOT NULL , 
    `customerProduct` VARCHAR(255) NOT NULL , 
    `quantityNeededForProduction` INT(11) NOT NULL , 
    `productionOrderDateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP , 
    PRIMARY KEY (`productionOrderID`)
) ENGINE = InnoDB;

CREATE TABLE `component_DB`.`component_stock` 
(
    `id` INT(11) NOT NULL AUTO_INCREMENT ,  
    `componentName` VARCHAR(255) NOT NULL , 
    `componentQuantity` INT NOT NULL , PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `purchasing_DB`.`purchasing_order` 
(
    `purchasingOrderID` INT(11) NOT NULL AUTO_INCREMENT ,
    `material` VARCHAR(255) NOT NULL ,
    `price` FLOAT(11) NOT NULL , 
    `vendor` VARCHAR(255) NOT NULL , 
    `amount` FLOAT(11) NOT NULL ,  
    `approved` VARCHAR(255) NOT NULL , 
    PRIMARY KEY (`purchasingOrderID`)
) ENGINE = InnoDB;


CREATE TABLE `warehouse`.`place` 
(
    `item_id` INT NOT NULL AUTO_INCREMENT ,
    `shelf_id` INT NOT NULL , 
    `place_id` INT NOT NULL , 
    `item` VARCHAR(45) NULL DEFAULT NULL , 
    `status` INT NULL DEFAULT NULL , 
    `last_change` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL , 
    `last_user` VARCHAR(45) NULL DEFAULT NULL , 
    PRIMARY KEY (`item_id`)
) ENGINE = InnoDB;

GRANT ALL PRIVILEGES ON customer_DB.* TO 'dev'@'%' IDENTIFIED BY 'dev';
FLUSH PRIVILEGES;

GRANT ALL PRIVILEGES ON finished_product_DB.* TO 'dev'@'%' IDENTIFIED BY 'dev';
FLUSH PRIVILEGES;

GRANT ALL PRIVILEGES ON production_order_DB.* TO 'dev'@'%' IDENTIFIED BY 'dev';
FLUSH PRIVILEGES;

GRANT ALL PRIVILEGES ON purchasing_DB.* TO 'dev'@'%' IDENTIFIED BY 'dev';
FLUSH PRIVILEGES;

GRANT ALL PRIVILEGES ON component_DB.* TO 'dev'@'%' IDENTIFIED BY 'dev';
FLUSH PRIVILEGES;

GRANT ALL PRIVILEGES ON warehouse.* TO 'dev'@'%' IDENTIFIED BY 'dev';
FLUSH PRIVILEGES;

USE finished_product_DB;
INSERT INTO `finished_product_stock` (`productName`, `productQuantity`) 
VALUES 
  ('Mountain Bike', '50'), 
  ('Hybrid 40000 Bicycle', '50'), 
  ('Speed Thriller Electric 147 Bicycle', '50');

USE component_DB;
INSERT INTO `component_stock` (`componentName`, `componentQuantity`) 
VALUES 
('Mountain bike frame', '50'),
('Hybrid bicycle wheels', '50'),
('Electric bicycle frame', '50');

USE warehouse;
INSERT INTO `place` (`shelf_id`, `place_id`, `item`, `status`)
VALUES 
(1,1,'Mountain Bike',1),
(1,2,'empty',0),
(1,3,'empty',0),
(1,4,'empty',0),
(1,5,'empty',0),
(1,6,'empty',0);