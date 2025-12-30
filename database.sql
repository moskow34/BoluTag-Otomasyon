-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: bolutagdb
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `trips`
--

DROP TABLE IF EXISTS `trips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trips` (
  `id` int NOT NULL AUTO_INCREMENT,
  `passenger_id` int DEFAULT NULL,
  `driver_id` int DEFAULT NULL,
  `source` varchar(50) COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `destination` varchar(50) COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `time_slot` varchar(20) COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb3_turkish_ci DEFAULT 'BEKLIYOR',
  PRIMARY KEY (`id`),
  KEY `passenger_id` (`passenger_id`),
  KEY `driver_id` (`driver_id`),
  CONSTRAINT `trips_ibfk_1` FOREIGN KEY (`passenger_id`) REFERENCES `users` (`id`),
  CONSTRAINT `trips_ibfk_2` FOREIGN KEY (`driver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trips`
--

LOCK TABLES `trips` WRITE;
/*!40000 ALTER TABLE `trips` DISABLE KEYS */;
/*!40000 ALTER TABLE `trips` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tc` varchar(20) COLLATE utf8mb3_turkish_ci NOT NULL,
  `password` varchar(50) COLLATE utf8mb3_turkish_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb3_turkish_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb3_turkish_ci NOT NULL,
  `location` varchar(50) COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  `plate` varchar(20) COLLATE utf8mb3_turkish_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tc` (`tc`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_turkish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'111','1234','CEO Yiğit Erkan','admin',NULL,NULL),(3,'101','1234','Kuzey (Aktaş)','sofor','Aktaş','14 KZ 489'),(4,'102','1234','Banu (Aktaş)','sofor','Aktaş','14 BN 109'),(5,'103','1234','Cemre (Aşağısoku)','sofor','Aşağısoku','14 CM 299'),(6,'104','1234','Simay (Aşağısoku)','sofor','Aşağısoku','14 SM 324'),(7,'105','1234','Zeynep (Bahçelievler)','sofor','Bahçelievler','14 ZY 441'),(8,'106','1234','Ferhat (Bahçelievler)','sofor','Bahçelievler','14 FR 444'),(9,'107','1234','Barış (Borazanlar)','sofor','Borazanlar','14 BR 747'),(10,'108','1234','Sami (Borazanlar)','sofor','Borazanlar','14 SM 665'),(11,'109','1234','Güney (Dağkent)','sofor','Dağkent','14 GN 123'),(12,'110','1234','Ramiz (Dağkent)','sofor','Dağkent','14 RM 001'),(13,'201','1234','Ömer (Karaçayır)','sofor','Karaçayır','14 OM 199'),(14,'202','1234','Kenan (Karaçayır)','sofor','Karaçayır','14 KN 511'),(15,'203','1234','Eyşan (İzzet Baysal)','sofor','İzzet Baysal','14 EY 723'),(16,'204','1234','Ezel (İzzet Baysal)','sofor','İzzet Baysal','14 EZ 630'),(17,'205','1234','Şebnem (Kültür)','sofor','Kültür','14 SB 611'),(18,'206','1234','Mert (Kültür)','sofor','Kültür','14 MT 933'),(19,'207','1234','Bahar (Seyit)','sofor','Seyit','14 BH 341'),(20,'208','1234','Cengiz (Seyit)','sofor','Seyit','14 CG 264'),(21,'209','1234','Serdar (Sümer)','sofor','Sümer','14 SR 999'),(22,'210','1234','Meliha (Sümer)','sofor','Sümer','14 ML 002'),(23,'301','1234','Mümtaz (Tabaklar)','sofor','Tabaklar','14 MZ 003'),(24,'302','1234','Ali (Tabaklar)','sofor','Tabaklar','14 AL 943');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-30 13:49:21
