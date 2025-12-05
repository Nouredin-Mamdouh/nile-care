USE nilecare_db;

-- Create student_progress table for tracking module completion
CREATE TABLE IF NOT EXISTS student_progress (
    progress_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    completion_percentage INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    last_accessed TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (module_id) REFERENCES learning_modules(module_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_module (user_id, module_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_last_accessed (last_accessed)
);

-- Create student_achievements table for badges/achievements
CREATE TABLE IF NOT EXISTS student_achievements (
    achievement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    achievement_type VARCHAR(50),
    earned_at TIMESTAMP NULL,
    is_earned BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_earned (is_earned)
);

-- Insert sample achievements for the first student
INSERT IGNORE INTO student_achievements (user_id, title, description, achievement_type, earned_at, is_earned) 
SELECT u.user_id, 'First Steps', 'Completed your first module', 'FIRST_MODULE', NOW(), TRUE 
FROM users u WHERE u.email = 'ammar@gmail.com'
LIMIT 1;

INSERT IGNORE INTO student_achievements (user_id, title, description, achievement_type, earned_at, is_earned) 
SELECT u.user_id, '7-Day Streak', 'Logged in for 7 consecutive days', 'STREAK_7_DAYS', NOW(), TRUE 
FROM users u WHERE u.email = 'ammar@gmail.com'
LIMIT 1;

INSERT IGNORE INTO student_achievements (user_id, title, description, achievement_type, is_earned) 
SELECT u.user_id, 'Module Master', 'Completed 5 modules', 'MODULE_MASTER', FALSE 
FROM users u WHERE u.email = 'ammar@gmail.com'
LIMIT 1;

INSERT IGNORE INTO student_achievements (user_id, title, description, achievement_type, is_earned) 
SELECT u.user_id, 'Assessment Pro', 'Completed all self-assessments', 'ASSESSMENT_PRO', FALSE 
FROM users u WHERE u.email = 'ammar@gmail.com'
LIMIT 1;

-- Insert sample progress data for testing
INSERT IGNORE INTO student_progress (user_id, module_id, completion_percentage, status, last_accessed) 
SELECT u.user_id, m.module_id, 75, 'IN_PROGRESS', '2025-12-04 11:00:00' 
FROM users u, learning_modules m 
WHERE u.email = 'ammar@gmail.com' AND m.title = 'Stress Management Fundamentals' LIMIT 1;

INSERT IGNORE INTO student_progress (user_id, module_id, completion_percentage, status, last_accessed) 
SELECT u.user_id, m.module_id, 30, 'IN_PROGRESS', '2025-12-04 03:00:00' 
FROM users u, learning_modules m 
WHERE u.email = 'ammar@gmail.com' AND m.title LIKE '%Mindfulness%' LIMIT 1;

INSERT IGNORE INTO student_progress (user_id, module_id, completion_percentage, status, last_accessed) 
SELECT u.user_id, m.module_id, 100, 'COMPLETED', '2025-11-28 14:30:00' 
FROM users u, learning_modules m 
WHERE u.email = 'ammar@gmail.com' LIMIT 1;
