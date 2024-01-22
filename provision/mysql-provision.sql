CREATE DATABASE finished_product_DB;
CREATE DATABASE production_order_DB;
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
    `productionOrderID` INT NOT NULL AUTO_INCREMENT , 
    `orderID` INT NOT NULL , 
    `quantityNeededForProduction` INT NOT NULL , 
    `productionOrderDateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP , 
    PRIMARY KEY (`productionOrderID`)
) ENGINE = InnoDB;


CREATE TABLE `warehouse`.`place` 
(
    `shelf_id` INT NOT NULL AUTO_INCREMENT , 
    `place_id` INT NOT NULL , 
    `item` VARCHAR(45) NULL DEFAULT NULL , 
    `status` INT NULL DEFAULT NULL , 
    `last_change` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL , 
    `last_user` VARCHAR(45) NULL DEFAULT NULL , 
    PRIMARY KEY (`shelf_id`)
) ENGINE = InnoDB;

GRANT ALL PRIVILEGES ON customer_DB.* TO 'dev'@'172.20.0.10' IDENTIFIED BY 'dev';
FLUSH PRIVILEGES;