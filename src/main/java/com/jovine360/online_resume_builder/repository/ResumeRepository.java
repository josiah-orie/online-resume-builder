package com.jovine360.online_resume_builder.repository;

import com.jovine360.online_resume_builder.models.Resume;
import com.jovine360.online_resume_builder.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserOrderByUpdatedAtDesc(User user);

    Optional<Resume> findByIdAndUser(Long id, User user);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.experiences LEFT JOIN FETCH r.educations LEFT JOIN FETCH r.skills WHERE r.id = :id AND r.user = :user")
    Optional<Resume> findByIdAndUserWithDetails(@Param("id") Long id, @Param("user") User user);

    long countByUser(User user);
    List<Resume> findAllByUser(User user);
    List<Resume> findAllByUserId(Long userId);

}
