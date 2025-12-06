package com.nilecare.repository;

import com.nilecare.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    /**
     * Find all lessons for a specific module, ordered by lesson_order
     */
    List<Lesson> findByModuleIdOrderByLessonOrder(Long moduleId);
    
    /**
     * Find a specific lesson by ID
     */
    Lesson findByLessonId(Long lessonId);
}
