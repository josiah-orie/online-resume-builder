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
import java.util.Arrays;

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
        // Create template information with descriptions and features
        List<TemplateInfo> templates = Arrays.asList(
            new TemplateInfo("default", "Default Template", 
                "Clean and professional layout suitable for most industries", 
                Arrays.asList("Two-column layout", "Professional typography", "Skill tags", "Project showcase"),
                "#4a6bff", "bi-file-earmark-text"),
            new TemplateInfo("modern", "Modern Template", 
                "Contemporary design with gradients and timeline styling", 
                Arrays.asList("Gradient headers", "Timeline experience", "Modern color palette", "Clean sections"),
                "#6c5ce7", "bi-lightning"),
            new TemplateInfo("professional", "Professional Template", 
                "Traditional and formal design ideal for corporate positions", 
                Arrays.asList("Serif typography", "Classic layout", "Formal styling", "Executive appearance"),
                "#2d3436", "bi-briefcase"),
            new TemplateInfo("creative", "Creative Template", 
                "Vibrant sidebar design perfect for creative professionals", 
                Arrays.asList("Colorful sidebar", "Skill visualization", "Creative fonts", "Dynamic layout"),
                "#fd79a8", "bi-palette"),
            new TemplateInfo("minimal", "Minimal Template", 
                "Clean minimalist approach with subtle styling", 
                Arrays.asList("Minimalist design", "Ample white space", "Clean typography", "Subtle accents"),
                "#74b9ff", "bi-circle")
        );
        
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

    @GetMapping("/preview/{templateId}")
    @PreAuthorize("isAuthenticated()")
    public String previewTemplate(@PathVariable String templateId, Model model, Authentication authentication) {
        // Create sample resume data for preview
        Resume sampleResume = createSampleResume(templateId, authentication.getName());
        
        model.addAttribute("resume", sampleResume);
        model.addAttribute("templateId", templateId);
        model.addAttribute("isPreview", true);
        
        return "resumes/preview";
    }

    private Resume createSampleResume(String templateId, String username) {
        Resume resume = new Resume();
        
        // Set sample data based on template
        switch (templateId.toLowerCase()) {
            case "default":
                resume.setTitle("John Developer - Full Stack Developer");
                resume.setSummary("Experienced software developer with 5+ years in full-stack development, specializing in Java, Spring Boot, and React.");
                break;
            case "modern":
                resume.setTitle("Jane Modern - UI/UX Designer");
                resume.setSummary("Creative UI/UX designer passionate about creating intuitive and beautiful digital experiences.");
                break;
            case "professional":
                resume.setTitle("Robert Executive - Senior Project Manager");
                resume.setSummary("Strategic project manager with 10+ years of experience leading cross-functional teams in enterprise environments.");
                break;
            case "creative":
                resume.setTitle("Alex Creative - Graphic Designer");
                resume.setSummary("Innovative graphic designer with expertise in branding, digital design, and creative storytelling.");
                break;
            case "minimal":
                resume.setTitle("Sarah Minimal - Product Manager");
                resume.setSummary("Results-driven product manager focused on user-centered design and data-driven decision making.");
                break;
            default:
                resume.setTitle("Sample Resume - Your Title Here");
                resume.setSummary("This is a sample resume to demonstrate the template design.");
        }
        
        // Add sample data (you can expand this as needed)
        return resume;
    }

    // Inner class for template information
    public static class TemplateInfo {
        private String id;
        private String name;
        private String description;
        private List<String> features;
        private String primaryColor;
        private String icon;
        
        public TemplateInfo(String id, String name, String description, List<String> features, String primaryColor, String icon) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.features = features;
            this.primaryColor = primaryColor;
            this.icon = icon;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public List<String> getFeatures() { return features; }
        public String getPrimaryColor() { return primaryColor; }
        public String getIcon() { return icon; }
    }
}