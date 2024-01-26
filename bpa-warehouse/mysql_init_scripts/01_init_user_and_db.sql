DROP USER IF EXISTS 'ftrobot'@'%';
DROP DATABASE IF EXISTS warehouse;

CREATE USER 'ftrobot'@'%' IDENTIFIED BY 'Camunda2024!';
GRANT ALL ON *.* TO 'ftrobot'@'%';
CREATE DATABASE warehouse CHARACTER SET 'utf8mb4';