package com.nilecare.repository;

import com.nilecare.model.StudentAchievement;
import com.nilecare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAchievementRepository extends JpaRepository<StudentAchievement, Long> {
    List<StudentAchievement> findByStudent(User student);
    List<StudentAchievement> findByStudentAndIsEarnedTrue(User student);
}
