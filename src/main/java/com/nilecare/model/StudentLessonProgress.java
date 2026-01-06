package com.nilecare.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_lesson_progress")
@Data
@NoArgsConstructor
public class StudentLessonProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This is the ID we are sorting by!

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "lesson_id")
    private Long lessonId;

    @Column(name = "module_id")
    private Long moduleId;

    @Column(name = "completed_at")
    private LocalDateTime completedAt = LocalDateTime.now();

    public StudentLessonProgress(Long userId, Long lessonId, Long moduleId) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.moduleId = moduleId;
    }
}