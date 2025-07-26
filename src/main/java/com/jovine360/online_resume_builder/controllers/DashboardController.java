// DashboardController.java
package com.jovine360.online_resume_builder.controllers;

import com.jovine360.online_resume_builder.models.Resume;
import com.jovine360.online_resume_builder.models.User;
import com.jovine360.online_resume_builder.services.ResumeService;
import com.jovine360.online_resume_builder.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final UserService userService;
    private final ResumeService resumeService;
    
    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        List<Resume> resumes = resumeService.findAllByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("resumes", resumes);
        
        return "dashboard/index";
    }
}
