-- MySQL dump 10.13  Distrib 8.0.38, for macos14 (x86_64)
--
-- Host: 127.0.0.1    Database: clovia
-- ------------------------------------------------------
-- Server version	9.2.0

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
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `productType` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `productName` varchar(255) NOT NULL,
  `productImage` varchar(255) DEFAULT NULL,
  `price` float(6,2) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'Necklace','Elara\'s Heart','assets/images/product1.jpg',85.00,'A delicate silver heart pendant with sparkling cubic zirconia, perfect for everyday elegance.'),(2,'Necklace','Rhiannon\'s Flow','assets/images/product2.jpg',95.00,'A sleek black onyx infinity symbol pendant, symbolizing eternal love and connection.'),(3,'Necklace','Coralie\'s Star','assets/images/product3.jpg',80.00,'A charming silver starfish pendant with tiny cubic zirconia, evoking a sense of summer and the sea.'),(4,'Necklace','Lyra\'s Dream','assets/images/product4.jpg',90.00,'A celestial-inspired necklace with a crescent moon and star pendant, symbolizing dreams and aspirations.'),(5,'Necklace','Sylvia\'s Roots','assets/images/product5.jpg',100.00,'A meaningful silver tree of life pendant, representing growth, strength, and connection.'),(6,'Bracelet','Zara\'s Joy','assets/images/product6.jpg',65.00,'A vibrant beaded stretch bracelet with a mix of colorful beads for a playful look.'),(7,'Bracelet','Isabelle\'s Keepsakes','assets/images/product7.jpg',75.00,'A customizable silver charm bracelet with various charms to express your personal style.'),(8,'Bracelet','Willow\'s Wing','assets/images/product8.jpg',70.00,'A stylish brown leather wrap bracelet with a silver feather charm, adding a touch of boho chic.'),(9,'Bracelet','Anya\'s Bond','assets/images/product9.jpg',60.00,'A colorful friendship bracelet made with embroidery floss, symbolizing kinship and connection.'),(10,'Bracelet','Clara\'s Gleam','assets/images/product10.jpg',80.00,'A classic wide silver bangle bracelet, adding a touch of elegance to any outfit.'),(11,'Ring','Lena\'s Spark','assets/images/product11.jpg',60.00,'An adjustable silver ring with a small cubic zirconia, perfect for everyday wear.'),(12,'Rings','Rowan\'s Trio','assets/images/product12.jpg',70.00,'A set of three stacking rings in different styles and widths, allowing for versatile looks.'),(13,'Ring','Evelyn\'s Promise','assets/images/product13.jpg',75.00,'A silver ring with an infinity symbol, representing endless love and commitment.'),(14,'Ring','Briar\'s Stone','assets/images/product14.jpg',85.00,'A personalized silver birthstone ring, celebrating a special month.'),(15,'Ring','Vivienne\'s Jewel','assets/images/product15.jpg',95.00,'A statement cocktail ring with a large, colorful gemstone, adding glamour to any occasion.'),(16,'Earrings','Mira\'s Lights','assets/images/product16.jpg',65.00,'Classic silver stud earrings, perfect for everyday simplicity and style.'),(17,'Earrings','Stella\'s Circles','assets/images/product17.jpg',70.00,'Stylish silver hoop earrings, a versatile accessory for any look.'),(18,'Earrings','Dahlia\'s Tears','assets/images/product18.jpg',80.00,'Elegant silver teardrop earrings with small cubic zirconia, adding a touch of sparkle.'),(19,'Earrings','Aurelia\'s Cascade','assets/images/product19.jpg',90.00,'Statement chandelier earrings with delicate chains and beads, creating a glamorous effect.'),(20,'Earrings','Nora\'s Embrace','assets/images/product20.jpg',75.00,'Trendy silver huggie earrings, comfortable and stylish for everyday wear.');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-03  0:16:46
