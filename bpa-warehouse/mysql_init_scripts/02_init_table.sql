-- Table structure for table `place`
DROP TABLE IF EXISTS `place`;
CREATE TABLE `place` (
  `shelf_id` int NOT NULL,
  `place_id` int NOT NULL,
  `item` varchar(45) DEFAULT NULL,
  `status` int DEFAULT NULL,
  `last_change` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_user` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`shelf_id`,`place_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- Dumping data for table `place`
LOCK TABLES `place` WRITE;
INSERT INTO `place` VALUES (1,1,'Bicycle',1,'2022-10-26 07:47:19','Warehouse Robot 1'),(1,2,'empty',0,'2022-10-31 14:32:51','Warehouse Robot 1'),(1,3,'empty',0,'2022-10-31 11:48:49','Warehouse Robot 1'),(1,4,'empty',0,'2022-05-11 10:49:02','Warehouse Robot 1'),(1,5,'empty',0,'2022-05-11 09:45:29','Warehouse Robot 1'),(1,6,'empty',0,'2022-05-11 09:49:59','Warehouse Robot 1');
UNLOCK TABLES;
