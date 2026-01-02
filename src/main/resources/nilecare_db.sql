-- 1. Create and Use the Database
CREATE DATABASE IF NOT EXISTS nilecare_db;
USE nilecare_db;

-- Disable foreign key checks temporarily to allow dropping/creating tables in any order
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================================
-- TABLE: users
-- ========================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users` (`user_id`, `email`, `password_hash`, `full_name`, `created_at`) VALUES 
(1,'admin@mindcare.com','$2a$10$gtAoo88GJ4GrpLBtESq3qeITRcqRT5ytlVJFih9PsFUJBQk6G3CSG','Admin User','2025-12-02 08:20:50'),
(2,'student@utm.my','$2a$10$gtAoo88GJ4GrpLBtESq3qeITRcqRT5ytlVJFih9PsFUJBQk6G3CSG','Student User','2025-12-02 08:20:50'),
(3,'ammar@gmail.com','$2a$10$gtAoo88GJ4GrpLBtESq3qeITRcqRT5ytlVJFih9PsFUJBQk6G3CSG','Ammar','2025-12-02 01:00:24'),
(4,'nouredin@gmail.com','$2a$10$DQB1epGpXGofoBRjFBCa4O0J1STHqphv5egfoFKvr9wVqjXMJ8XLm','Nouredin','2025-12-02 16:33:53'),
(5,'ammar12321@gmail.com','$2a$10$/1BFVqWB0Z/LsNWJLYBrPuZ8btS9nRJW3PHC/7Nf8Jk.wlf9p.SD.','fesfsefsf','2025-12-07 05:22:11');

-- ========================================================
-- TABLE: roles
-- ========================================================
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `roles` (`role_id`, `name`) VALUES 
(3,'ROLE_ADMIN'),
(2,'ROLE_COUNSELOR'),
(1,'ROLE_STUDENT');

-- ========================================================
-- TABLE: user_roles (Junction Table)
-- ========================================================
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES 
(2,1), (3,1), (5,1), -- Students
(1,3), (4,3);        -- Admins

-- ========================================================
-- TABLE: learning_modules
-- ========================================================
DROP TABLE IF EXISTS `learning_modules`;
CREATE TABLE `learning_modules` (
  `module_id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` text,
  `content_url` varchar(255) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `difficulty_level` varchar(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `learning_modules` (`module_id`, `title`, `description`, `content_url`, `category`, `difficulty_level`, `created_at`, `updated_at`) VALUES 
(1,'Stress Management Fundamentals','Learn effective techniques to identify, manage, and reduce stress in your daily life through connection with nature.','https://images.unsplash.com/photo-1472214103451-9374bd1c798e?auto=format&fit=crop&w=800&q=80','Stress & Anxiety','Beginner','2025-12-06 12:23:11','2025-12-06 12:23:11'),
(2,'Mindfulness & Meditation','Discover the power of mindfulness to improve mental clarity and reduce anxiety using simple breathing techniques.','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80','Mindfulness','Beginner','2025-12-06 12:23:11','2025-12-06 12:23:11'),
(3,'Emotional Regulation Skills','Master techniques to understand and manage your emotions effectively in high-pressure situations.','https://images.unsplash.com/photo-1477346611705-65d1883cee1e?auto=format&fit=crop&w=800&q=80','Emotional Health','Intermediate','2025-12-06 12:23:11','2025-12-06 12:23:11'),
(5,'The Power of Journaling','Explore how writing down your thoughts can help process complex emotions and reduce daily anxiety.','https://images.unsplash.com/photo-1517842645767-c639042777db?auto=format&fit=crop&w=800&q=80','Self-Help','Beginner','2025-12-06 12:23:11','2025-12-06 12:23:11'),
(6,'Exercise for Mental Health','Learn the biological connection between physical activity and mental well-being, with simple workout plans.','https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=800&q=80','Physical Health','Intermediate','2025-12-06 12:23:11','2025-12-06 12:23:11'),
(7,'Overcoming Procrastination','Practical strategies to beat the habit of delaying tasks and reduce academic stress.','https://images.unsplash.com/photo-1506784983877-45594efa4cbe?auto=format&fit=crop&w=800&q=80','Productivity','Advanced','2025-12-06 12:23:11','2025-12-06 12:23:11');

-- ========================================================
-- TABLE: lessons
-- ========================================================
DROP TABLE IF EXISTS `lessons`;
CREATE TABLE `lessons` (
  `lesson_id` bigint NOT NULL AUTO_INCREMENT,
  `module_id` bigint NOT NULL,
  `title` varchar(200) NOT NULL,
  `description` text,
  `content` text,
  `video_url` varchar(255) DEFAULT NULL,
  `lesson_order` int NOT NULL,
  `duration_minutes` int DEFAULT '25',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lessonId` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`lesson_id`),
  KEY `module_id` (`module_id`),
  CONSTRAINT `lessons_ibfk_1` FOREIGN KEY (`module_id`) REFERENCES `learning_modules` (`module_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `lessons` (`lesson_id`, `module_id`, `title`, `description`, `lesson_order`, `duration_minutes`, `created_at`) VALUES 
(1,3,'Introduction to Stress','Understanding what stress is and its impact on mental health.',1,20,'2025-12-06 13:20:34'),
(2,3,'Identifying Stressors','Learn to identify personal stress triggers and patterns.',2,25,'2025-12-06 13:20:34'),
(3,3,'Coping Strategies','Effective coping mechanisms for managing stress.',3,25,'2025-12-06 13:20:34'),
(4,3,'Practical Exercises','Hands-on exercises to practice stress management techniques.',4,30,'2025-12-06 13:20:34'),
(5,3,'Knowledge Check','Test your understanding of stress management concepts.',5,15,'2025-12-06 13:20:34');

-- ========================================================
-- TABLE: counselor_availability
-- ========================================================
DROP TABLE IF EXISTS `counselor_availability`;
CREATE TABLE `counselor_availability` (
  `slot_id` bigint NOT NULL AUTO_INCREMENT,
  `counselor_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `is_booked` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`slot_id`),
  KEY `counselor_id` (`counselor_id`),
  CONSTRAINT `counselor_availability_ibfk_1` FOREIGN KEY (`counselor_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- TABLE: appointments
-- ========================================================
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments` (
  `appointment_id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `slot_id` bigint NOT NULL,
  `status` varchar(20) DEFAULT 'CONFIRMED',
  `notes` text,
  PRIMARY KEY (`appointment_id`),
  UNIQUE KEY `slot_id` (`slot_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`slot_id`) REFERENCES `counselor_availability` (`slot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ========================================================
-- TABLE: help_requests
-- ========================================================
DROP TABLE IF EXISTS `help_requests`;
CREATE TABLE `help_requests` (
  `request_id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `message` text NOT NULL,
  `response` text,
  `status` varchar(255) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`request_id`),
  KEY `FK_user_help_request` (`user_id`),
  CONSTRAINT `FK_user_help_request` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `help_requests` (`request_id`, `category`, `created_at`, `message`, `response`, `status`, `subject`, `updated_at`, `user_id`) VALUES 
(3,'Technical Issue','2025-12-05 23:54:30','I keep getting an error when trying to open Module 3.','Issue fixed. Please relogin.','RESOLVED','Cannot access Module 3','2025-12-05 23:54:30',3),
(4,'Content Question','2025-12-05 23:54:30','I would like more information about the breathing exercises.','We are reviewing your request.','IN_PROGRESS','Question about mindfulness','2025-12-05 23:54:30',3),
(5,'Booking Issue','2025-12-05 23:54:30','The calendar is not showing any available slots.',NULL,'PENDING','Cannot book appointment','2025-12-05 23:54:30',3),
(29,'Technical Issue','2025-12-07 17:08:07','I cannot make a new account it say an error',NULL,'PENDING','the register isn\'t working','2025-12-07 17:08:07',3),
(30,'Technical Issue','2025-12-07 17:09:51','my acdcount registeration is shwoing errors',NULL,'PENDING','I cannot refiste an account','2025-12-07 17:09:51',3);

-- ========================================================
-- TABLE: student_achievements
-- ========================================================
DROP TABLE IF EXISTS `student_achievements`;
CREATE TABLE `student_achievements` (
  `achievement_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `achievement_type` varchar(50) DEFAULT NULL,
  `earned_at` timestamp NULL DEFAULT NULL,
  `is_earned` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`achievement_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_earned` (`is_earned`),
  CONSTRAINT `student_achievements_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `student_achievements` (`achievement_id`, `user_id`, `title`, `description`, `achievement_type`, `earned_at`, `is_earned`) VALUES 
(1,2,'First Steps','Completed your first module','FIRST_MODULE','2025-12-05 11:05:52',1),
(2,2,'7-Day Streak','Logged in for 7 consecutive days','STREAK_7_DAYS','2025-12-05 11:05:52',1),
(3,2,'Module Master','Completed 5 modules','MODULE_MASTER',NULL,0),
(4,2,'Assessment Pro','Completed all self-assessments','ASSESSMENT_PRO',NULL,0),
(9,3,'First Steps','Completed your first module','FIRST_MODULE','2025-12-05 11:36:18',1),
(10,3,'7-Day Streak','Logged in for 7 consecutive days','STREAK_7_DAYS','2025-12-05 11:36:18',1);

-- ========================================================
-- TABLE: student_feedback
-- ========================================================
DROP TABLE IF EXISTS `student_feedback`;
CREATE TABLE `student_feedback` (
  `feedback_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `category` varchar(100) NOT NULL,
  `rating` int NOT NULL,
  `subject` varchar(255) NOT NULL,
  `message` longtext NOT NULL,
  `status` enum('PENDING','REVIEWED','RESPONDED') NOT NULL DEFAULT 'PENDING',
  `response` longtext,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`feedback_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `student_feedback_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `student_feedback_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `student_feedback` (`feedback_id`, `user_id`, `category`, `rating`, `subject`, `message`, `status`, `response`, `created_at`, `updated_at`) VALUES 
(1,3,'Learning Modules',5,'Excellent stress management module','The stress management module was incredibly helpful.','RESPONDED','Thank you!','2025-12-04 03:00:00','2025-12-05 02:30:00'),
(2,3,'Platform Experience',4,'Great platform','Love the interface!','REVIEWED',NULL,'2025-12-03 19:00:00','2025-12-05 01:15:00'),
(3,3,'Counseling Service',5,'Amazing counseling session','Dr. Williams was very professional.','PENDING',NULL,'2025-11-28 06:30:00','2025-11-28 06:30:00');

-- ========================================================
-- TABLE: student_progress
-- ========================================================
DROP TABLE IF EXISTS `student_progress`;
CREATE TABLE `student_progress` (
  `progress_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `module_id` bigint NOT NULL,
  `completion_percentage` int NOT NULL DEFAULT '0',
  `status` varchar(50) NOT NULL DEFAULT 'NOT_STARTED',
  `last_accessed` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`progress_id`),
  UNIQUE KEY `unique_user_module` (`user_id`,`module_id`),
  KEY `module_id` (`module_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `student_progress_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `student_progress_ibfk_2` FOREIGN KEY (`module_id`) REFERENCES `learning_modules` (`module_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `student_progress` (`progress_id`, `user_id`, `module_id`, `completion_percentage`, `status`, `last_accessed`) VALUES 
(1,2,1,75,'IN_PROGRESS','2025-12-05 11:05:52'),
(2,2,2,30,'IN_PROGRESS','2025-12-04 11:05:52'),
(7,3,1,75,'IN_PROGRESS','2025-12-05 11:36:18'),
(8,3,2,30,'IN_PROGRESS','2025-12-04 11:36:18');

-- Restore foreign key checks
SET FOREIGN_KEY_CHECKS = 1;