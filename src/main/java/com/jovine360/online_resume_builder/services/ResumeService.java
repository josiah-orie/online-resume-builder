// ResumeService.java
package com.jovine360.online_resume_builder.services;

import com.jovine360.online_resume_builder.models.Resume;
import com.jovine360.online_resume_builder.models.User;

import java.util.List;
import java.util.Optional;

public interface ResumeService {
    Resume createResume(Resume resume, Long userId);
    Resume updateResume(Resume resume);
    void deleteResume(Long id);
    Optional<Resume> findById(Long id);
    List<Resume> findAllByUser(User user);
    List<Resume> findAllByUserId(Long userId);
}
