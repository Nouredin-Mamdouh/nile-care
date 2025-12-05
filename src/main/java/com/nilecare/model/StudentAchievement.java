package com.nilecare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Long achievementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "achievement_type")
    @Enumerated(EnumType.STRING)
    private AchievementType type;

    @Column(name = "earned_at")
    private LocalDateTime earnedAt;

    @Column(name = "is_earned")
    private Boolean isEarned = false;

    public enum AchievementType {
        FIRST_MODULE,
        STREAK_7_DAYS,
        MODULE_MASTER,
        ASSESSMENT_PRO,
        PERFECT_SCORE,
        CONSISTENCY_WEEK,
        COMMUNITY_HELPER
    }

    @PrePersist
    protected void onCreate() {
        if (isEarned && earnedAt == null) {
            earnedAt = LocalDateTime.now();
        }
    }
}
