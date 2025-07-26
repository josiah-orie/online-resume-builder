package com.jovine360.online_resume_builder.services.impl;

import com.jovine360.online_resume_builder.models.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFService {
    
    private final TemplateEngine templateEngine;
    
    /**
     * Generate a PDF using the default template
     */
    public byte[] generatePDF(Resume resume) throws IOException {
        return generatePDF(resume, "default");
    }
    
    /**
     * Generate a PDF using the specified template
     *
     * @param resume The resume data
     * @param template The template name to use
     * @return PDF document as byte array
     */
    public byte[] generatePDF(Resume resume, String template) {
        try {
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
        } catch (Exception e) {
            log.error("Error generating PDF for template: {}", template, e);
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Configure the renderer with appropriate styles and fonts based on the template
     * 
     * @param renderer The ITextRenderer instance
     * @param template The template name being used
     */
    private void setupRendererStyles(ITextRenderer renderer, String template) {
        try {
            switch (template.toLowerCase()) {
                case "creative":
                    setupCreativeTemplateFonts(renderer);
                    break;
                case "modern":
                    setupModernTemplateFonts(renderer);
                    break;
                case "professional":
                    setupProfessionalTemplateFonts(renderer);
                    break;
                case "minimal":
                    setupMinimalTemplateFonts(renderer);
                    break;
                default:
                    setupDefaultTemplateFonts(renderer);
                    break;
            }
            
            // Common configurations for all templates
            setupCommonStyles(renderer);
            
        } catch (Exception e) {
            log.warn("Could not load custom fonts for template: {}. Using default fonts.", template, e);
            setupFallbackFonts(renderer);
        }
    }
    
    /**
     * Setup fonts specifically for the creative template
     */
    private void setupCreativeTemplateFonts(ITextRenderer renderer) throws Exception {
        // Add modern, stylistic fonts for creative template
        
        // Try to load Open Sans for body text
        try {
            ClassPathResource openSansRegular = new ClassPathResource("static/fonts/OpenSans-Regular.ttf");
            if (openSansRegular.exists()) {
                renderer.getFontResolver().addFont(
                    openSansRegular.getURL().toString(), 
                    "OpenSans", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
        } catch (Exception e) {
            log.debug("OpenSans font not found, using fallback");
        }
        
        // Try to load Montserrat for headings
        try {
            ClassPathResource montserratBold = new ClassPathResource("static/fonts/Montserrat-Bold.ttf");
            if (montserratBold.exists()) {
                renderer.getFontResolver().addFont(
                    montserratBold.getURL().toString(), 
                    "Montserrat", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
        } catch (Exception e) {
            log.debug("Montserrat font not found, using fallback");
        }
        
        // Try to load Poppins for accent text
        try {
            ClassPathResource poppinsLight = new ClassPathResource("static/fonts/Poppins-Light.ttf");
            if (poppinsLight.exists()) {
                renderer.getFontResolver().addFont(
                    poppinsLight.getURL().toString(), 
                    "Poppins", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
        } catch (Exception e) {
            log.debug("Poppins font not found, using fallback");
        }
    }
    
    /**
     * Setup fonts for the modern template
     */
    private void setupModernTemplateFonts(ITextRenderer renderer) throws Exception {
        // Load Roboto family for modern look
        try {
            ClassPathResource robotoRegular = new ClassPathResource("static/fonts/Roboto-Regular.ttf");
            ClassPathResource robotoBold = new ClassPathResource("static/fonts/Roboto-Bold.ttf");
            
            if (robotoRegular.exists()) {
                renderer.getFontResolver().addFont(
                    robotoRegular.getURL().toString(), 
                    "Roboto", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
            
            if (robotoBold.exists()) {
                renderer.getFontResolver().addFont(
                    robotoBold.getURL().toString(), 
                    "Roboto-Bold", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
        } catch (Exception e) {
            log.debug("Roboto fonts not found, using fallback");
        }
    }
    
    /**
     * Setup fonts for the professional template
     */
    private void setupProfessionalTemplateFonts(ITextRenderer renderer) throws Exception {
        // Load serif fonts for professional appearance
        try {
            ClassPathResource sourceSerifRegular = new ClassPathResource("static/fonts/SourceSerif4-Regular.ttf");
            ClassPathResource sourceSerifBold = new ClassPathResource("static/fonts/SourceSerif4-Bold.ttf");
            
            if (sourceSerifRegular.exists()) {
                renderer.getFontResolver().addFont(
                    sourceSerifRegular.getURL().toString(), 
                    "SourceSerif", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
            
            if (sourceSerifBold.exists()) {
                renderer.getFontResolver().addFont(
                    sourceSerifBold.getURL().toString(), 
                    "SourceSerif-Bold", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
        } catch (Exception e) {
            log.debug("Source Serif fonts not found, using fallback");
        }
    }
    
    /**
     * Setup fonts for the minimal template
     */
    private void setupMinimalTemplateFonts(ITextRenderer renderer) throws Exception {
        // Load clean, minimal fonts
        try {
            ClassPathResource interRegular = new ClassPathResource("static/fonts/Inter-Regular.ttf");
            ClassPathResource interMedium = new ClassPathResource("static/fonts/Inter-Medium.ttf");
            
            if (interRegular.exists()) {
                renderer.getFontResolver().addFont(
                    interRegular.getURL().toString(), 
                    "Inter", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
            
            if (interMedium.exists()) {
                renderer.getFontResolver().addFont(
                    interMedium.getURL().toString(), 
                    "Inter-Medium", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
        } catch (Exception e) {
            log.debug("Inter fonts not found, using fallback");
        }
    }
    
    /**
     * Setup fonts for the default template
     */
    private void setupDefaultTemplateFonts(ITextRenderer renderer) throws Exception {
        // Load standard professional fonts
        try {
            ClassPathResource latoRegular = new ClassPathResource("static/fonts/Lato-Regular.ttf");
            ClassPathResource latoBold = new ClassPathResource("static/fonts/Lato-Bold.ttf");
            
            if (latoRegular.exists()) {
                renderer.getFontResolver().addFont(
                    latoRegular.getURL().toString(), 
                    "Lato", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
            
            if (latoBold.exists()) {
                renderer.getFontResolver().addFont(
                    latoBold.getURL().toString(), 
                    "Lato-Bold", 
                    "UTF-8", 
                    true, 
                    null
                );
            }
        } catch (Exception e) {
            log.debug("Lato fonts not found, using fallback");
        }
    }
    
    /**
     * Setup common styles and configurations for all templates
     */
    private void setupCommonStyles(ITextRenderer renderer) {
        // Set common rendering options
        renderer.getSharedContext().setPrint(true);
        renderer.getSharedContext().setInteractive(false);
        
        // Enable better text rendering
        renderer.getSharedContext().getTextRenderer().setSmoothingThreshold(0);
        
        // Set page size and margins programmatically if needed
        // This could be template-specific in the future
    }
    
    /**
     * Setup fallback fonts when custom fonts are not available
     */
    private void setupFallbackFonts(ITextRenderer renderer) {
        try {
            // Use system fonts as fallback
            renderer.getFontResolver().addFont("Helvetica", true);
            renderer.getFontResolver().addFont("Times-Roman", true);
            renderer.getFontResolver().addFont("Courier", true);
        } catch (Exception e) {
            log.warn("Could not load fallback fonts", e);
        }
    }
    
    /**
     * Load a font from classpath resources
     * 
     * @param fontPath Path to the font file in resources
     * @param fontFamily Font family name to register
     * @param renderer The ITextRenderer instance
     */
    private void loadFontFromResources(String fontPath, String fontFamily, ITextRenderer renderer) {
        try {
            ClassPathResource fontResource = new ClassPathResource(fontPath);
            if (fontResource.exists()) {
                try (InputStream fontStream = fontResource.getInputStream()) {
                    renderer.getFontResolver().addFont(
                        fontResource.getURL().toString(),
                        fontFamily,
                        "UTF-8",
                        true,
                        null
                    );
                    log.debug("Successfully loaded font: {} as {}", fontPath, fontFamily);
                }
            } else {
                log.debug("Font file not found: {}", fontPath);
            }
        } catch (Exception e) {
            log.warn("Failed to load font: {} as {}", fontPath, fontFamily, e);
        }
    }
}