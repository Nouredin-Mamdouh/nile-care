-- Learning Modules Migration
-- This file seeds sample learning modules into the database

-- Insert sample learning modules
INSERT INTO learning_modules (title, description, content_url, category, difficulty_level) VALUES
(
    'Understanding Mental Health Fundamentals',
    'Learn the basics of mental health, common disorders, and the importance of mental wellness in everyday life.',
    '/nile-care/static/images/module-mental-health.jpg',
    'Mental Wellness',
    'Beginner'
),
(
    'Emotional Intelligence and Self-Awareness',
    'Develop your emotional intelligence through self-reflection exercises and practical techniques to understand your emotions better.',
    '/nile-care/static/images/module-emotional-intelligence.jpg',
    'Self-Awareness',
    'Intermediate'
),
(
    'Understanding and Managing Stress',
    'Comprehensive strategies for identifying stress triggers, understanding physiological effects, and developing effective coping mechanisms.',
    '/nile-care/static/images/module-stress-management.jpg',
    'Mental Wellness',
    'Intermediate'
),
(
    'Anxiety Management Techniques',
    'Learn evidence-based techniques to manage anxiety, including breathing exercises, cognitive behavioral therapy principles, and mindfulness practices.',
    '/nile-care/static/images/module-anxiety.jpg',
    'Anxiety',
    'Beginner'
),
(
    'Building Healthy Relationships',
    'Explore communication skills, boundaries, conflict resolution, and the foundations of healthy interpersonal relationships.',
    '/nile-care/static/images/module-relationships.jpg',
    'Relationships',
    'Intermediate'
),
(
    'Sleep Hygiene and Better Rest',
    'Master the science of sleep, create better sleep habits, and learn techniques to improve sleep quality and overall wellness.',
    '/nile-care/static/images/module-sleep.jpg',
    'Wellness',
    'Beginner'
),
(
    'Mindfulness and Meditation Basics',
    'Introduction to mindfulness practices, meditation techniques, and how to incorporate these into your daily routine for mental clarity.',
    '/nile-care/static/images/module-mindfulness.jpg',
    'Mindfulness',
    'Beginner'
),
(
    'Cognitive Behavioral Therapy Principles',
    'Understand the core principles of CBT and learn how to identify and challenge negative thinking patterns.',
    '/nile-care/static/images/module-cbt.jpg',
    'Mental Wellness',
    'Advanced'
),
(
    'Resilience and Coping with Change',
    'Develop resilience skills to navigate life changes, setbacks, and challenges with greater confidence and emotional strength.',
    '/nile-care/static/images/module-resilience.jpg',
    'Personal Growth',
    'Intermediate'
),
(
    'Depression: Recognition and Support',
    'Learn to recognize signs of depression, understand the condition better, and discover available support resources and treatment options.',
    '/nile-care/static/images/module-depression.jpg',
    'Mental Health',
    'Beginner'
);

-- Update learning_modules with additional nullable fields if needed
ALTER TABLE learning_modules ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE learning_modules ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Create lessons table if it doesn't exist
CREATE TABLE IF NOT EXISTS lessons (
    lesson_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    content TEXT,
    video_url VARCHAR(255),
    lesson_order INT NOT NULL,
    duration_minutes INT DEFAULT 25,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (module_id) REFERENCES learning_modules(module_id) ON DELETE CASCADE
);

-- Insert sample lessons for Module 3 (Managing Stress)
INSERT INTO lessons (module_id, title, description, lesson_order, duration_minutes) VALUES
(3, 'Introduction to Stress', 'Understanding what stress is and its impact on mental health.', 1, 20),
(3, 'Identifying Stressors', 'Learn to identify personal stress triggers and patterns.', 2, 25),
(3, 'Coping Strategies', 'Effective coping mechanisms for managing stress.', 3, 25),
(3, 'Practical Exercises', 'Hands-on exercises to practice stress management techniques.', 4, 30),
(3, 'Knowledge Check', 'Test your understanding of stress management concepts.', 5, 15);
