package com.jovine360.online_resume_builder.services.impl;

import com.jovine360.online_resume_builder.models.Resume;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PDFService {
    
    private final TemplateEngine templateEngine;
    
    public byte[] generatePDF(Resume resume) throws IOException {
        Context context = new Context();
        context.setVariable("resume", resume);
        
        String html = templateEngine.process("pdf/resume-template", context);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
        
        return outputStream.toByteArray();
    }
    /**
     * Generate a PDF using the specified template
     *
     * @param resume The resume data
     * @param template The template name to use
     * @return PDF document as byte array
     */
    public byte[] generatePDF(Resume resume, String template) {
        // Validate template name to prevent path traversal attacks
        if (template == null || template.isEmpty() || template.contains("/") || template.contains("\\")) {
            template = "default";
        }

        // Set up context with resume data
        Context context = new Context();
        context.setVariable("resume", resume);
        context.setVariable("templateName", template);

        // Determine the template path based on the template parameter
        String templatePath = switch (template.toLowerCase()) {
            case "modern" -> "pdf/resume-template-modern";
            case "professional" -> "pdf/resume-template-professional";
            case "creative" -> "pdf/resume-template-creative";
            case "minimal" -> "pdf/resume-template-minimal";
            default -> "pdf/resume-template";
        };

        // Process the template with the context
        String html = templateEngine.process(templatePath, context);

        // Render the HTML to PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);

        // Set up fonts and styles
        setupRendererStyles(renderer, template);

        // Render PDF
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Configure the renderer with appropriate styles based on the template
     */
    private void setupRendererStyles(ITextRenderer renderer, String template) {
        // Add any template-specific styling or fonts
        // This could be extended to load different CSS resources based on template

        // Example: For the creative template, we might want to add custom fonts
        if ("creative".equals(template)) {
            // Add custom font directories or styling
            // renderer.getFontResolver().addFont(...);
        }

        // You could add different font resolvers or CSS resources based on the template
    }


}