package com.nilecare.repository;

import com.nilecare.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    // Finds specific lesson by its ID
    Lesson findByLessonId(Long lessonId);

    // FIXED: Changed 'findByModule_ModuleId' to 'findByModuleId'
    // We search directly by the 'moduleId' Long field defined in your Lesson class.
    List<Lesson> findByModuleIdOrderByLessonOrder(Long moduleId);

    // FIXED: Simplified the Query
    // We check 'l.moduleId' directly because there is no 'l.module' object.
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.moduleId = :moduleId")
    long countTotalLessonsInModule(@Param("moduleId") Long moduleId);
}