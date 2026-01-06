package com.nilecare.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id") // <--- CRITICAL FIX: Maps to your actual DB column
    private Long lessonId;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "video_url", length = 255)
    private String videoUrl;

    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Getters
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public Integer getLessonOrder() { return lessonOrder; }
    public void setLessonOrder(Integer lessonOrder) { this.lessonOrder = lessonOrder; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}