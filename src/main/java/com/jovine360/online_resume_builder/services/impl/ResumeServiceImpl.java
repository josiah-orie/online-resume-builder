package com.jovine360.online_resume_builder.services.impl;

import com.jovine360.online_resume_builder.models.Resume;
import com.jovine360.online_resume_builder.models.User;
import com.jovine360.online_resume_builder.repository.ResumeRepository;
import com.jovine360.online_resume_builder.repository.UserRepository;
import com.jovine360.online_resume_builder.services.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {
    
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    
    @Override
    public Resume createResume(Resume resume, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        resume.setUser(user);
        return resumeRepository.save(resume);
    }
    
    @Override
    public Resume updateResume(Resume resume) {
        return resumeRepository.save(resume);
    }
    
    @Override
    public void deleteResume(Long id) {
        resumeRepository.deleteById(id);
    }
    
    @Override
    public Optional<Resume> findById(Long id) {
        return resumeRepository.findById(id);
    }
    
    @Override
    public List<Resume> findAllByUser(User user) {
        return resumeRepository.findAllByUser(user);
    }
    
    @Override
    public List<Resume> findAllByUserId(Long userId) {
        return resumeRepository.findAllByUserId(userId);
    }
}