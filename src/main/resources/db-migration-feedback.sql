-- Create student_feedback table
CREATE TABLE IF NOT EXISTS student_feedback (
    feedback_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(100) NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    subject VARCHAR(255) NOT NULL,
    message LONGTEXT NOT NULL,
    status ENUM('PENDING', 'REVIEWED', 'RESPONDED') DEFAULT 'PENDING' NOT NULL,
    response LONGTEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- Insert sample feedback data
INSERT INTO student_feedback (user_id, category, rating, subject, message, status, created_at, updated_at, response) 
VALUES (
    (SELECT user_id FROM users WHERE email = 'ammar@gmail.com' LIMIT 1),
    'Learning Modules',
    5,
    'Excellent stress management module',
    'The stress management module was incredibly helpful. The breathing exercises really work!',
    'RESPONDED',
    '2025-12-04 11:00:00',
    '2025-12-05 10:30:00',
    'Thank you for your positive feedback! We\'re glad the module was helpful.'
);

INSERT INTO student_feedback (user_id, category, rating, subject, message, status, created_at, updated_at) 
VALUES (
    (SELECT user_id FROM users WHERE email = 'ammar@gmail.com' LIMIT 1),
    'Platform Experience',
    4,
    'Great platform, minor suggestions',
    'Love the interface! Would be great to have a mobile app version.',
    'REVIEWED',
    '2025-12-04 03:00:00',
    '2025-12-05 09:15:00'
);

INSERT INTO student_feedback (user_id, category, rating, subject, message, status, created_at, updated_at) 
VALUES (
    (SELECT user_id FROM users WHERE email = 'ammar@gmail.com' LIMIT 1),
    'Counseling Service',
    5,
    'Amazing counseling session',
    'Dr. Williams was very professional and understanding. Highly recommend!',
    'PENDING',
    '2025-11-28 14:30:00',
    '2025-11-28 14:30:00'
);
