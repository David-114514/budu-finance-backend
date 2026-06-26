-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: budu_finance_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `type` enum('ASSET','LIABILITY','OFFSET') NOT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `current_balance` decimal(15,2) DEFAULT '0.00',
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (6,'CurrentBalance','OFFSET','HKD',467810.00,1,'2026-05-09 12:08:33');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `type` enum('INCOME','EXPENSE','TRANSFER','PARENT_CONTRIB','PARENT_REPAY') NOT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (8,'轉入按揭戶口','TRANSFER','🏦','2026-05-09 19:39:01.000000'),(9,'按揭戶口支出','EXPENSE','🛒','2026-05-09 19:39:01.000000');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `category_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `mortgage_interest_saved` decimal(10,2) DEFAULT '0.00',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `transaction_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (8,'2026-02-27',200000.00,8,3,'First deposit I',0.00,'2026-05-31 16:51:02'),(9,'2026-02-27',-200000.00,9,3,'First deposit I',0.00,'2026-05-31 16:51:02'),(10,'2026-03-04',203100.00,8,3,'First deposit II',0.00,'2026-05-31 16:51:02'),(11,'2026-03-04',-203100.00,9,3,'First deposit II',0.00,'2026-05-31 16:51:02'),(12,'2026-04-08',35156.00,8,4,'4500USD',0.00,'2026-05-31 16:51:02'),(17,'2026-04-28',-1892.10,9,4,'Fire insurance',0.00,'2026-05-31 16:51:02'),(18,'2026-05-05',30000.00,8,4,'USD',0.00,'2026-05-31 16:51:02'),(19,'2026-05-12',40000.00,8,1,'Agency fee',0.00,'2026-05-31 16:51:02'),(20,'2026-05-12',-40000.00,9,1,'Agency fee',0.00,'2026-05-31 16:51:02'),(21,'2026-05-31',33000.00,8,4,'USD',0.00,'2026-05-31 16:51:02'),(22,'2026-05-31',12000.00,8,2,'bubu deposit',0.00,'2026-05-31 16:51:02'),(23,'2026-05-30',50000.00,8,1,'dudu deposit',0.00,'2026-05-31 16:51:02'),(24,'2026-05-29',50000.00,8,1,'dudu deposit',0.00,'2026-05-31 16:51:02'),(25,'2026-05-30',50000.00,8,3,'Dudu parents deposit',0.00,'2026-05-31 16:51:02'),(26,'2026-05-31',50000.00,8,3,'Dudu parents deposit',0.00,'2026-05-31 16:51:02'),(27,'2026-05-04',806610.00,8,3,'First deposit III',0.00,'2026-05-31 16:51:02'),(28,'2026-05-04',-806610.00,9,3,'First deposit III',0.00,'2026-05-31 16:51:02'),(29,'2026-05-31',-49659.00,9,4,'Furniture and so on',0.00,'2026-05-31 16:58:14'),(30,'2026-05-31',9958.40,8,1,'Shopping by RMB',0.00,'2026-05-31 17:05:27'),(31,'2026-06-01',50000.00,8,3,'Dudu parents deposit',0.00,'2026-06-12 15:22:59'),(32,'2026-06-02',50000.00,8,3,'Dudu parents deposit',0.00,'2026-06-12 15:23:16'),(33,'2026-06-03',50000.00,8,3,'Dudu parents deposit',0.00,'2026-06-12 15:23:32'),(34,'2026-06-08',45000.00,8,3,'Dudu parents deposit',0.00,'2026-06-12 15:23:52'),(35,'2026-06-09',-1301.00,9,1,'Electricity bill',0.00,'2026-06-12 15:24:50'),(36,'2026-06-09',-315.00,9,1,'Internet bill',0.00,'2026-06-12 15:25:17'),(37,'2026-06-12',5780.00,8,3,'Dudu parents deposit',0.00,'2026-06-12 15:30:33'),(38,'2026-06-01',82.70,8,1,' Interest',0.00,'2026-06-12 16:18:38');
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `role` enum('HUSBAND','WIFE','PARENT') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'dudu','HUSBAND','2026-04-12 03:30:08','123456'),(2,'bubu','WIFE','2026-04-12 03:30:08','123456'),(3,'duduP','PARENT','2026-04-12 03:30:08','123456'),(4,'bubuP','PARENT','2026-04-12 03:30:08','123456');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'budu_finance_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-12 16:34:18
