// ResumeController.java
package com.jovine360.online_resume_builder.controllers;

import com.jovine360.online_resume_builder.models.*;
import com.jovine360.online_resume_builder.services.ResumeService;
import com.jovine360.online_resume_builder.services.UserService;
import com.jovine360.online_resume_builder.services.impl.PDFService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeController {
    
    private final ResumeService resumeService;
    private final UserService userService;
    private final PDFService pdfService;
    
    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createResumeForm(Model model) {
        model.addAttribute("resume", new Resume());
        return "resumes/create";
    }
    
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createResume(@Valid @ModelAttribute("resume") Resume resume,
                              BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "resumes/create";
        }
        
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        resumeService.createResume(resume, user.getId());
        return "redirect:/dashboard";
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String viewResume(@PathVariable Long id, 
                            @RequestParam(required = false) String template,
                            Model model, Authentication authentication) {
        Resume resume = resumeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        
        // Ensure that the authenticated user owns this resume
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!resume.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("resume", resume);
        
        // If a specific template is requested, use that template
        if (template != null && !template.isEmpty()) {
            return "resumes/resume-template";
        }
        
        // Default view
        return "resumes/view";
    }
    
    @GetMapping("/{id}/preview")
    @PreAuthorize("isAuthenticated()")
    public String previewResume(@PathVariable Long id, 
                               @RequestParam(required = false, defaultValue = "default") String template,
                               Model model, Authentication authentication) {
        Resume resume = resumeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        
        // Ensure that the authenticated user owns this resume
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!resume.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("resume", resume);
        model.addAttribute("templateName", template);
        
        return "resumes/resume-template";
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editResumeForm(@PathVariable Long id, Model model, Authentication authentication) {
        Resume resume = resumeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        
        // Ensure that the authenticated user owns this resume
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!resume.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("resume", resume);
        return "resumes/edit";
    }
    
    @PostMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateResume(@PathVariable Long id,
                              @Valid @ModelAttribute("resume") Resume resume,
                              BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "resumes/edit";
        }
        
        // Ensure that the authenticated user owns this resume
        Resume existingResume = resumeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!existingResume.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        
        resume.setId(id);
        resume.setUser(user);
        resumeService.updateResume(resume);
        
        return "redirect:/resumes/" + id;
    }
    
    @GetMapping("/{id}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteResume(@PathVariable Long id, Authentication authentication) {
        Resume resume = resumeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        
        // Ensure that the authenticated user owns this resume
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!resume.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        
        resumeService.deleteResume(id);
        return "redirect:/dashboard";
    }
    
    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadResumePDF(
            @PathVariable Long id, 
            @RequestParam(required = false, defaultValue = "default") String template,
            Authentication authentication) {
        try {
            Resume resume = resumeService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
            
            // Ensure that the authenticated user owns this resume
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            
            if (!resume.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().build();
            }
            
            // Pass the template parameter to the PDF service
//            byte[] pdfBytes = pdfService.generatePDF(resume, template);
            byte[] pdfBytes = pdfService.generatePDF(resume);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", resume.getTitle() + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/templates")
    @PreAuthorize("isAuthenticated()")
    public String viewTemplates(Model model) {
        // Add a list of available templates
        List<String> templates = List.of("default", "modern", "professional", "creative", "minimal");
        model.addAttribute("templates", templates);
        return "resumes/templates";
    }
    
    @GetMapping("/{id}/share")
    @PreAuthorize("isAuthenticated()")
    public String getShareableLink(@PathVariable Long id, Model model, Authentication authentication) {
        Resume resume = resumeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
        
        // Ensure that the authenticated user owns this resume
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        
        if (!resume.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard";
        }
        
        // Generate a shareable link (this could be a token-based URL in a real app)
        String shareableLink = "/resumes/shared/" + id;
        model.addAttribute("shareableLink", shareableLink);
        model.addAttribute("resume", resume);
        
        return "resumes/share";
    }
}