-- MySQL dump 10.13  Distrib 9.3.0, for macos15.2 (arm64)
--
-- Host: localhost    Database: vinasocial_user
-- ------------------------------------------------------
-- Server version	9.3.0

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
-- Current Database: `vinasocial_user`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `vinasocial_user` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `vinasocial_user`;

--
-- Table structure for table `auth_otps`
--

DROP TABLE IF EXISTS `auth_otps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_otps` (
  `id` int NOT NULL AUTO_INCREMENT,
  `public_id` char(36) NOT NULL,
  `code` varchar(6) DEFAULT NULL,
  `created_at` varchar(19) NOT NULL,
  `expired_at` varchar(19) NOT NULL,
  `purpose` varchar(14) NOT NULL,
  `verified_at` varchar(19) DEFAULT NULL,
  `user_id` int NOT NULL,
  `username_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjgp5mvjuidmu0j8ar13d9xd4x` (`user_id`),
  KEY `FKtmvcqen5tccoy3vjq15obe3g8` (`username_id`),
  CONSTRAINT `FKjgp5mvjuidmu0j8ar13d9xd4x` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKtmvcqen5tccoy3vjq15obe3g8` FOREIGN KEY (`username_id`) REFERENCES `usernames` (`id`),
  CONSTRAINT `auth_otps_chk_1` CHECK ((`purpose` in (_utf8mb4'REGISTER',_utf8mb4'VERIFY_EMAIL',_utf8mb4'CHANGE_EMAIL',_utf8mb4'VERIFY_PHONE',_utf8mb4'CHANGE_PHONE',_utf8mb4'LOGIN',_utf8mb4'RESET_PASSWORD')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_otps`
--

LOCK TABLES `auth_otps` WRITE;
/*!40000 ALTER TABLE `auth_otps` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_otps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth_sessions`
--

DROP TABLE IF EXISTS `auth_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth_sessions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` varchar(19) NOT NULL,
  `expired_at` varchar(19) NOT NULL,
  `username` varchar(255) NOT NULL,
  `refresh_token` mediumtext NOT NULL,
  `revoked_at` varchar(19) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpu507182mdfutajr71rgk67l` (`user_id`),
  CONSTRAINT `FKpu507182mdfutajr71rgk67l` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth_sessions`
--

LOCK TABLES `auth_sessions` WRITE;
/*!40000 ALTER TABLE `auth_sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_links`
--

DROP TABLE IF EXISTS `profile_links`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile_links` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `url` varchar(255) NOT NULL,
  `profile_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1msaxsm6uvuqgu3qkf4yoarnl` (`profile_id`),
  CONSTRAINT `FK1msaxsm6uvuqgu3qkf4yoarnl` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_links`
--

LOCK TABLES `profile_links` WRITE;
/*!40000 ALTER TABLE `profile_links` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_links` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_notification_settings`
--

DROP TABLE IF EXISTS `profile_notification_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile_notification_settings` (
  `profile_id` int NOT NULL,
  `best_friend_enabled` bit(1) NOT NULL,
  `collaborator_enabled` bit(1) NOT NULL,
  `follow_enabled` bit(1) NOT NULL,
  `friend_enabled` bit(1) NOT NULL,
  `message_enabled` bit(1) NOT NULL,
  `page_following_enabled` bit(1) NOT NULL,
  `post_enabled` bit(1) NOT NULL,
  `reply_comment_enabled` bit(1) NOT NULL,
  `story_enabled` bit(1) NOT NULL,
  `tag_enabled` bit(1) NOT NULL,
  PRIMARY KEY (`profile_id`),
  CONSTRAINT `FK4d2t0p8oenwkeoajh3gk06k3e` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_notification_settings`
--

LOCK TABLES `profile_notification_settings` WRITE;
/*!40000 ALTER TABLE `profile_notification_settings` DISABLE KEYS */;
INSERT INTO `profile_notification_settings`
  (`profile_id`, `best_friend_enabled`, `collaborator_enabled`,
    `follow_enabled`, `friend_enabled`, `message_enabled`,
    `page_following_enabled`, `post_enabled`, `reply_comment_enabled`,
    `story_enabled`, `tag_enabled`)
VALUES (1, 1,1,1,1,1,1,1,1,1,1),
(2, 1,1,1,1,1,1,1,1,1,1),
(3, 1,1,1,1,1,1,1,1,1,1),
(4, 1,1,1,1,1,1,1,1,1,1),
(5, 1,1,1,1,1,1,1,1,1,1),
(6, 1,1,1,1,1,1,1,1,1,1),
(7, 1,1,1,1,1,1,1,1,1,1),
(8, 1,1,1,1,1,1,1,1,1,1),
(9, 1,1,1,1,1,1,1,1,1,1),
(10, 1,1,1,1,1,1,1,1,1,1),
(11, 1,1,1,1,1,1,1,1,1,1),
(12, 1,1,1,1,1,1,1,1,1,1),
(13, 1,1,1,1,1,1,1,1,1,1),
(14, 1,1,1,1,1,1,1,1,1,1),
(15, 1,1,1,1,1,1,1,1,1,1),
(16, 1,1,1,1,1,1,1,1,1,1),
(17, 1,1,1,1,1,1,1,1,1,1),
(18, 1,1,1,1,1,1,1,1,1,1),
(19, 1,1,1,1,1,1,1,1,1,1),
(20, 1,1,1,1,1,1,1,1,1,1);
/*!40000 ALTER TABLE `profile_notification_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_privacy_settings`
--

DROP TABLE IF EXISTS `profile_privacy_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile_privacy_settings` (
    `profile_id` INT NOT NULL,
    `is_private` BIT(1) NOT NULL,
    `show_profile_permission` VARCHAR(12) NOT NULL,
    `show_followings_permission` VARCHAR(12) NOT NULL,
    `show_followers_permission` VARCHAR(12) NOT NULL,
    `show_highlight_stories_permission` VARCHAR(12) NOT NULL,
    `show_posts_permission` VARCHAR(12) NOT NULL,
    `show_reposts_permission` VARCHAR(12) NOT NULL,
    `show_saves_permission` VARCHAR(12) NOT NULL,
    `send_message_permission` VARCHAR(12) NOT NULL,

    PRIMARY KEY (`profile_id`),
    CONSTRAINT `FK_profile_privacy_settings_profile`
        FOREIGN KEY (`profile_id`)
        REFERENCES `profiles` (`id`),
    CONSTRAINT `CK_profile_privacy_settings_permission`
        CHECK (
            `show_profile_permission` IN (
                'EVERYONE',
                'BEST_FRIENDS',
                'FRIENDS',
                'FOLLOWERS',
                'PRIVATE'
            )
            AND `show_followings_permission` IN (
                'EVERYONE',
                'BEST_FRIENDS',
                'FRIENDS',
                'FOLLOWERS',
                'PRIVATE'
            )
            AND `show_followers_permission` IN (
                'EVERYONE',
                'BEST_FRIENDS',
                'FRIENDS',
                'FOLLOWERS',
                'PRIVATE'
            )
            AND `show_highlight_stories_permission` IN (
                'EVERYONE',
                'BEST_FRIENDS',
                'FRIENDS',
                'FOLLOWERS',
                'PRIVATE'
            )
            AND `show_posts_permission` IN (
                'EVERYONE',
                'BEST_FRIENDS',
                'FRIENDS',
                'FOLLOWERS',
                'PRIVATE'
            )
            AND `show_reposts_permission` IN (
                'EVERYONE',
                'BEST_FRIENDS',
                'FRIENDS',
                'FOLLOWERS',
                'PRIVATE'
            )
            AND `show_saves_permission` IN (
                'EVERYONE',
                'BEST_FRIENDS',
                'FRIENDS',
                'FOLLOWERS',
                'PRIVATE'
            )
            AND `send_message_permission` IN (
                'EVERYONE',
                'BEST_FRIENDS',
                'FRIENDS',
                'FOLLOWERS',
                'PRIVATE'
            )
        )
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_privacy_settings`
--

LOCK TABLES `profile_privacy_settings` WRITE;
/*!40000 ALTER TABLE `profile_privacy_settings` DISABLE KEYS */;
INSERT INTO `profile_privacy_settings` (
    `profile_id`,
    `is_private`,
    `show_profile_permission`,
    `show_followings_permission`,
    `show_followers_permission`,
    `show_highlight_stories_permission`,
    `show_posts_permission`,
    `show_reposts_permission`,
    `show_saves_permission`,
    `send_message_permission`
)
VALUES
(1,  0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(2,  0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(3,  0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(4,  1, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(5,  0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(6,  1, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(7,  0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(8,  0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(9,  1, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(10, 0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(11, 0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(12, 1, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(13, 0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(14, 0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(15, 1, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(16, 0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(17, 0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(18, 1, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(19, 0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE'),
(20, 0, 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE', 'EVERYONE');
/*!40000 ALTER TABLE `profile_privacy_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_stats`
--

DROP TABLE IF EXISTS `profile_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile_stats` (
  `profile_id` int NOT NULL,
  `best_friend_count` int NOT NULL,
  `follower_count` int NOT NULL,
  `following_count` int NOT NULL,
  `friend_count` int NOT NULL,
  `media_count` int NOT NULL,
  `updated_count` int NOT NULL,
  PRIMARY KEY (`profile_id`),
  CONSTRAINT `FKmnb3tpvqce6ivakx6dg8iao33` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_stats`
--

LOCK TABLES `profile_stats` WRITE;
/*!40000 ALTER TABLE `profile_stats` DISABLE KEYS */;
INSERT INTO `profile_stats`
  (`profile_id`, `best_friend_count`,
    `follower_count`, `following_count`,
    `friend_count`, `media_count`, `updated_count`)
VALUES (1, 0, 0, 0, 0, 0, 0),
  (2, 0, 0, 0, 0, 0, 0),
  (3, 0, 0, 0, 0, 0, 0),
  (4, 0, 0, 0, 0, 0, 0),
  (5, 0, 0, 0, 0, 0, 0),
  (6, 0, 0, 0, 0, 0, 0),
  (7, 0, 0, 0, 0, 0, 0),
  (8, 0, 0, 0, 0, 0, 0),
  (9, 0, 0, 0, 0, 0, 0),
  (10, 0, 0, 0, 0, 0, 0),
  (11, 0, 0, 0, 0, 0, 0),
  (12, 0, 0, 0, 0, 0, 0),
  (13, 0, 0, 0, 0, 0, 0),
  (14, 0, 0, 0, 0, 0, 0),
  (15, 0, 0, 0, 0, 0, 0),
  (16, 0, 0, 0, 0, 0, 0),
  (17, 0, 0, 0, 0, 0, 0),
  (18, 0, 0, 0, 0, 0, 0),
  (19, 0, 0, 0, 0, 0, 0),
  (20, 0, 0, 0, 0, 0, 0);
/*!40000 ALTER TABLE `profile_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profiles`
--

DROP TABLE IF EXISTS `profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profiles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `avatar` varchar(26) NULL,
  `bio` text NULL,
  `birthdate` varchar(10) NOT NULL,
  `email` varchar(255) NULL,
  `first_name` varchar(255) NOT NULL,
  `gender` varchar(6) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `note` text NULL,
  `phone` varchar(11) NULL,
  `username` varchar(100) NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlnk8iosvsrn5614xw3lgnybgk` (`email`),
  UNIQUE KEY `UKd0qpa98358lh5hh9dl645ejm1` (`phone`),
  UNIQUE KEY `UKgkg1kpmjy7f1hm6ugsoxmgisq` (`username`),
  UNIQUE KEY `UK4ixsj6aqve5pxrbw2u0oyk8bb` (`user_id`),
  CONSTRAINT `FK410q61iev7klncmpqfuo85ivh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `profiles_chk_1` CHECK ((`gender` in (_utf8mb4'MALE',_utf8mb4'FEMALE')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profiles`
--

LOCK TABLES `profiles` WRITE;
/*!40000 ALTER TABLE `profiles` DISABLE KEYS */;
INSERT INTO `profiles` (`id`, `alias`, `avatar`, `bio`, `birthdate`, `email`, `first_name`, `gender`, `last_name`, `note`, `phone`, `username`, `user_id`)
VALUES (1, 'Nguyễn Văn A', NULL, 'Xin chào, mình là A 👋', '2000-01-15', 'vana@gmail.com', 'Văn A', 'MALE', 'Nguyễn', NULL, '0901000001', 'nguyenvana', 2),
  (2, 'Trần Thị Bình', NULL, 'Photography and coffee ☕📷', '2001-02-20', 'binh@gmail.com', 'Thị Bình', 'FEMALE', 'Trần', NULL, '0901000002', 'tranthibinh', 3),
  (3, 'Lê Văn Quang', NULL, 'Developer | Coffee | Travel', '1999-03-12', 'quang@gmail.com', 'Văn Quang', 'MALE', 'Lê', NULL, '0901000003', 'levanquang', 4),
  (4, 'Phạm Minh Đức', NULL, 'Build something people love.', '2000-04-18', 'duc@gmail.com', 'Minh Đức', 'MALE', 'Phạm', NULL, '0901000004', 'phamminhduc', 5),
  (5, 'Hoàng Thùy', NULL, 'Life is better with music 🎵', '2002-05-25', 'thuy@gmail.com', 'Thùy', 'FEMALE', 'Hoàng', NULL, '0901000005', 'hoangthuy', 6),
  (6, 'Vũ Minh Trí', NULL, 'Football and friends ⚽', '1998-06-10', 'tri@gmail.com', 'Minh Trí', 'MALE', 'Vũ', NULL, '0911000006', 'vuminhtri', 7),
  (7, 'Đặng Ngọc Anh', NULL, 'Travel around Vietnam 🇻🇳', '2001-07-08', 'ngocanh@gmail.com', 'Ngọc Anh', 'FEMALE', 'Đặng', NULL, '0911000007', 'dangngocanh', 8),
  (8, 'Bùi Hoàng Nam', NULL, 'Backend developer ☕', '1999-08-16', 'hoangnam@gmail.com', 'Hoàng Nam', 'MALE', 'Bùi', NULL, '0911000008', 'buihoangnam', 9),
  (9, 'Đoàn Anh Khoa', NULL, 'Student | Developer | Gamer', '2002-09-01', 'khoa@gmail.com', 'Anh Khoa', 'MALE', 'Đoàn', NULL, '0911000009', 'doanhkhoa', 10),
  (10, 'Nguyễn Linh', NULL, 'Just another human 🌱', '2001-10-11', 'linh@gmail.com', 'Linh', 'FEMALE', 'Nguyễn', NULL, '0911000010', 'nguyenlinh', 11),
  (11, 'Trần Minh Quân', NULL, 'Code. Sleep. Repeat.', '2000-11-19', 'quan@gmail.com', 'Minh Quân', 'MALE', 'Trần', NULL, '0911000011', 'tranminhquan', 12),
  (12, 'Lê Thùy Tiên', NULL, 'Books and peaceful days 📚', '2002-12-03', 'tien@gmail.com', 'Thùy Tiên', 'FEMALE', 'Lê', NULL, '0911000012', 'lethuytien', 13),
  (13, 'Phạm Tuấn Kiệt', NULL, 'Software engineer in progress.', '1998-01-22', 'kiet@gmail.com', 'Tuấn Kiệt', 'MALE', 'Phạm', NULL, '0911000013', 'phamtuankiet', 14),
  (14, 'Hoàng Phương', NULL, 'Good vibes only ✨', '2001-02-14', 'phuong@gmail.com', 'Phương', 'FEMALE', 'Hoàng', NULL, '0911000014', 'hoangphuong', 15),
  (15, 'Võ Thanh Sơn', NULL, 'Adventure begins outside your comfort zone.', '1999-03-27', 'son@gmail.com', 'Thanh Sơn', 'MALE', 'Võ', NULL, '0911000015', 'vothanhson', 16),
  (16, 'Ngọc Diệp', NULL, 'Art, coffee and sunshine ☀️', '2002-04-30', 'diep@gmail.com', 'Diệp', 'FEMALE', 'Ngọc', NULL, '0911000016', 'ngocdiep', 17),
  (17, 'Minh Châu', NULL, 'Keep learning.', '2000-05-17', 'chau@gmail.com', 'Minh Châu', 'FEMALE', 'Minh', NULL, '0911000017', 'minhchau', 18),
  (18, 'Quốc Bảo', NULL, 'Tech enthusiast 🚀', '1998-06-29', 'bao@gmail.com', 'Quốc Bảo', 'MALE', 'Quốc', NULL, '0911000018', 'quocbao', 19),
  (19, 'Thanh Hằng', NULL, 'Enjoy every little moment.', '2001-07-21', 'hang@gmail.com', 'Thanh Hằng', 'FEMALE', 'Thanh', NULL, '0911000019', 'thanhhang', 20),
  (20, 'Anh Kiệt', NULL, 'Coffee first, code later ☕', '1999-08-09', 'kiet2@gmail.com', 'Anh Kiệt', 'MALE', 'Anh', NULL, '0911000020', 'anhkiet', 21);

/*!40000 ALTER TABLE `profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usernames`
--

DROP TABLE IF EXISTS `usernames`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usernames` (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `is_verify` bit(1) NOT NULL,
  `type` varchar(5) NOT NULL,
  `value` varchar(255) NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhlqrh1aa2cd6vpycu5ypqcpkk` (`value`),
  KEY `FKrl7fbemgnmfar1a6aktdkh0ma` (`user_id`),
  CONSTRAINT `FKrl7fbemgnmfar1a6aktdkh0ma` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `usernames_chk_1` CHECK ((`type` in (_utf8mb4'PHONE',_utf8mb4'TEXT',_utf8mb4'EMAIL')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usernames`
--

LOCK TABLES `usernames` WRITE;
/*!40000 ALTER TABLE `usernames` DISABLE KEYS */;
INSERT INTO `usernames` (`id`, `is_active`, `is_verify`, `type`, `value`, `user_id`)
VALUES (1, 1, 1, 'TEXT', 'admin', 1),
  (2, 1, 1, 'TEXT', 'nguyenvana', 2),
  (3, 1, 1, 'TEXT', 'tranthibinh', 3),
  (4, 1, 1, 'TEXT', 'levanquang', 4),
  (5, 1, 1, 'TEXT', 'phamminhduc', 5),
  (6, 1, 1, 'TEXT', 'hoangthuy', 6),
  (7, 1, 1, 'TEXT', 'vuminhtri', 7),
  (8, 1, 1, 'TEXT', 'dangngocanh', 8),
  (9, 1, 1, 'TEXT', 'buihoangnam', 9),
  (10, 1, 1, 'TEXT', 'doanhkhoa', 10),
  (11, 1, 1, 'TEXT', 'nguyenlinh', 11),
  (12, 1, 1, 'TEXT', 'tranminhquan', 12),
  (13, 1, 1, 'TEXT', 'lethuytien', 13),
  (14, 1, 1, 'TEXT', 'phamtuankiet', 14),
  (15, 1, 1, 'TEXT', 'hoangphuong', 15),
  (16, 1, 1, 'TEXT', 'vothanhson', 16),
  (17, 1, 1, 'TEXT', 'ngocdiep', 17),
  (18, 1, 1, 'TEXT', 'minhchau', 18),
  (19, 1, 1, 'TEXT', 'quocbao', 19),
  (20, 1, 1, 'TEXT', 'thanhhang', 20),
  (21, 1, 1, 'TEXT', 'anhkiet', 21),
  (22, 1, 1, 'EMAIL', 'vana@gmail.com', 2),
  (23, 1, 1, 'EMAIL', 'binh@gmail.com', 3),
  (24, 1, 1, 'EMAIL', 'quang@gmail.com', 4),
  (25, 1, 1, 'EMAIL', 'duc@gmail.com', 5),
  (26, 1, 1, 'EMAIL', 'thuy@gmail.com', 6),
  (27, 1, 1, 'EMAIL', 'tri@gmail.com', 7),
  (28, 1, 1, 'EMAIL', 'ngocanh@gmail.com', 8),
  (29, 1, 1, 'EMAIL', 'hoangnam@gmail.com', 9),
  (30, 1, 1, 'EMAIL', 'khoa@gmail.com', 10),
  (31, 1, 1, 'EMAIL', 'linh@gmail.com', 11),
  (32, 1, 1, 'PHONE', '0901000001', 2),
  (33, 1, 1, 'PHONE', '0901000002', 3),
  (34, 1, 1, 'PHONE', '0901000003', 4),
  (35, 1, 1, 'PHONE', '0901000004', 5),
  (36, 1, 1, 'PHONE', '0901000005', 6);
/*!40000 ALTER TABLE `usernames` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` varchar(19) NOT NULL,
  `last_login_at` varchar(19) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `public_id` varchar(10) NOT NULL,
  `role` varchar(6) NOT NULL,
  `status` varchar(8) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKs24bux761rbgowsl7a4b386ba` (`public_id`),
  CONSTRAINT `users_chk_1` CHECK ((`role` in (_utf8mb4'ADMIN',_utf8mb4'MEMBER'))),
  CONSTRAINT `users_chk_2` CHECK ((`status` in (_utf8mb4'FLAGGED',_utf8mb4'ACTIVE',_utf8mb4'INACTIVE',_utf8mb4'BANDED')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `created_at`, `last_login_at`, `password`, `public_id`, `role`, `status`)
VALUES (1, '2026-09-05 00:00:00', NULL, '$2a$12$X6usFrUh7JsPnM3RxTkzpeFGgDb52vYJIxdIP2ulYLKtbTX3Sm40G', 'USER-ADMIN', 'ADMIN', 'ACTIVE'),
  (2, '2026-09-05 00:00:00', NULL, '$2a$12$Yt2bbO6yITEli49HTnx1rOe72enbmFl6WzrEWsoBohJe097LBMuUO', 'USER-7K2M9', 'MEMBER', 'ACTIVE'),
  (3, '2026-09-05 00:00:00', NULL, '$2a$12$IE1VbOVOeiZE7V.YnrRkx.j/TXr8FzP0O8X55i4HZSQUecIAZGNJu', 'USER-P8L4T', 'MEMBER', 'ACTIVE'),
  (4, '2026-09-05 00:00:00', NULL, '$2a$12$Z.Q0Feh2iyHaeD/lmKqY1uPPrRZXX/GNTz8IjMnSOga/HcZHmVE4y', 'USER-X3Q9K', 'MEMBER', 'ACTIVE'),
  (5, '2026-09-05 00:00:00', NULL, '$2a$12$SSEfgMqvaFSmPt1bPRU2H.hfEdWXmuwOy8MrZZpcgRr/42/AvuFYC', 'USER-M2R8P', 'MEMBER', 'ACTIVE'),
  (6, '2026-09-05 00:00:00', NULL, '$2a$12$wfcjp5l6uNxVas754CoonOw1z2TI740hxzsEvoB9vDwvzqEVndGxa', 'USER-T6N4V', 'MEMBER', 'ACTIVE'),
  (7, '2026-09-05 00:00:00', NULL, '$2a$12$NWOempIpoph10QnGttA5XOWctThHtboEC1jZm5u0WbEYYMO.EhR4q', 'USER-Q8W2L', 'MEMBER', 'ACTIVE'),
  (8, '2026-09-05 00:00:00', NULL, '$2a$12$nctwLLeHmvS9t8/S38uxF.39QVdeh/lYT8njFuappRZHTmE6LqbdO', 'USER-G3P6K', 'MEMBER', 'ACTIVE'),
  (9, '2026-09-05 00:00:00', NULL, '$2a$12$FSF09AtiVzX8.xOCGS1q0ukjruYPkCiY9oevJXazTqzVB1xuFK6Rq', 'USER-H5X9M', 'MEMBER', 'ACTIVE'),
  (10, '2026-09-05 00:00:00', NULL, '$2a$12$mxZS4r6KE66ynS0WuYGzAOHn8n9a1bBQbrGqGZgb1nX7yPzq.E0ei', 'USER-J7K4W', 'MEMBER', 'ACTIVE'),
  (11, '2026-09-05 00:00:00', NULL, '$2a$12$bNo6yavA0W3bKRJDVEBtq.GAhO8xjIUkK/tVRzVjt/p2OqiFOhKZu', 'USER-K2Q6R', 'MEMBER', 'ACTIVE'),
  (12, '2026-09-05 00:00:00', NULL, '$2a$12$FUK7ZiXX5Z8A2jcucrwws.T8/uoi4SeOJwKShHR8DU0EahxYh7AFq', 'USER-L8T3Z', 'MEMBER', 'ACTIVE'),
  (13, '2026-09-05 00:00:00', NULL, '$2a$12$bLDvxRKlcByv/xTFg13uRuRAAzDOfNLW50OQKXMeEdYdul5s.X2I.', 'USER-M4P7X', 'MEMBER', 'ACTIVE'),
  (14, '2026-09-05 00:00:00', NULL, '$2a$12$EMoyw1EvcQGNxyxXAXWXCOHZ7Z6boLM00XGnGmM7stwLGo/YWL3aS', 'USER-N9R5W', 'MEMBER', 'ACTIVE'),
  (15, '2026-09-05 00:00:00', NULL, '$2a$12$KyYUl7uXqxUwOjgrGa.BEucyjwHLMcJu1n181/.qVkA41ten4fLMW', 'USER-P3X8T', 'MEMBER', 'ACTIVE'),
  (16, '2026-09-05 00:00:00', NULL, '$2a$12$ykBJvVH1OomPC4DdZTQrLeUZ8FRt.Fq16KGddC6oy.g7Z6PqBzy5K', 'USER-Q6N2V', 'MEMBER', 'ACTIVE'),
  (17, '2026-09-05 00:00:00', NULL, '$2a$12$BF1LjJC3O3hFu93DDBeWZuHLUpp9a1fCgE4wzSd/S90vrArhuoCdC', 'USER-R8W4M', 'MEMBER', 'ACTIVE'),
  (18, '2026-09-05 00:00:00', NULL, '$2a$12$Rbx2LDS6WZQ67B4P8Y0J0u57FcoIBW9DkDOje2FPnIm25za.xhm.e', 'USER-T5K7N', 'MEMBER', 'ACTIVE'),
  (19, '2026-09-05 00:00:00', NULL, '$2a$12$Py4hLnNvu8J6uui4y0DNGuDbc7f1K16n3uNOzWthMqb.eR8ETOgWe', 'USER-V2M6R', 'MEMBER', 'ACTIVE'),
  (20, '2026-09-05 00:00:00', NULL, '$2a$12$1Jx4.VE.s/0u5j0XQs.6seP8szbbTnblXxtNuA7967mvgoVesjifW', 'USER-W9L4K', 'MEMBER', 'ACTIVE'),
  (21, '2026-09-05 00:00:00', NULL, '$2a$12$bY1RBK0u9.ef9cmSwkY1H.f2oIEz8303mPRNMyrfKN7Nbst8vbs3.', 'USER-X3Q8M', 'MEMBER', 'ACTIVE');
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

-- Dump completed on 2026-09-05  6:49:10
