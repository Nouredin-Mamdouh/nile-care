package com.nilecare.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "learning_modules")
@Data
public class LearningModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Long moduleId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "content_url")
    private String contentUrl;

    private String category; // e.g., Anxiety, Stress

    @Column(name = "difficulty_level")
    private String difficultyLevel;

    // Convenience getter for Thymeleaf template usage
    public Long getId() {
        return this.moduleId;
    }
}