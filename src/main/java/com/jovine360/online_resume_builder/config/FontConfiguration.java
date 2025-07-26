
package com.jovine360.online_resume_builder.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "pdf.fonts")
@Data
public class FontConfiguration {
    
    private Map<String, TemplateFonts> templates = new HashMap<>();
    private boolean enableCustomFonts = true;
    private String fontBasePath = "static/fonts/";
    
    @Data
    public static class TemplateFonts {
        private List<FontDefinition> fonts;
    }
    
    @Data
    public static class FontDefinition {
        private String family;
        private String file;
        private String weight = "normal";
        private String style = "normal";
        private boolean primary = false;
    }
    
    public FontConfiguration() {
        initializeDefaultFonts();
    }
    
    private void initializeDefaultFonts() {
        // Initialize default font configurations for each template
        // This could also be loaded from application.yml
        
        // Creative template fonts
        TemplateFonts creativeFonts = new TemplateFonts();
        creativeFonts.setFonts(List.of(
            createFontDefinition("OpenSans", "OpenSans-Regular.ttf", "normal", "normal", true),
            createFontDefinition("Montserrat", "Montserrat-Bold.ttf", "bold", "normal", false),
            createFontDefinition("Poppins", "Poppins-Light.ttf", "300", "normal", false)
        ));
        templates.put("creative", creativeFonts);
        
        // Modern template fonts
        TemplateFonts modernFonts = new TemplateFonts();
        modernFonts.setFonts(List.of(
            createFontDefinition("Roboto", "Roboto-Regular.ttf", "normal", "normal", true),
            createFontDefinition("Roboto", "Roboto-Bold.ttf", "bold", "normal", false)
        ));
        templates.put("modern", modernFonts);
        
        // Professional template fonts
        TemplateFonts professionalFonts = new TemplateFonts();
        professionalFonts.setFonts(List.of(
            createFontDefinition("SourceSerif", "SourceSerif4-Regular.ttf", "normal", "normal", true),
            createFontDefinition("SourceSerif", "SourceSerif4-Bold.ttf", "bold", "normal", false)
        ));
        templates.put("professional", professionalFonts);
        
        // Minimal template fonts
        TemplateFonts minimalFonts = new TemplateFonts();
        minimalFonts.setFonts(List.of(
            createFontDefinition("Inter", "Inter-Regular.ttf", "normal", "normal", true),
            createFontDefinition("Inter", "Inter-Medium.ttf", "500", "normal", false)
        ));
        templates.put("minimal", minimalFonts);
        
        // Default template fonts
        TemplateFonts defaultFonts = new TemplateFonts();
        defaultFonts.setFonts(List.of(
            createFontDefinition("Lato", "Lato-Regular.ttf", "normal", "normal", true),
            createFontDefinition("Lato", "Lato-Bold.ttf", "bold", "normal", false)
        ));
        templates.put("default", defaultFonts);
    }
    
    private FontDefinition createFontDefinition(String family, String file, String weight, String style, boolean primary) {
        FontDefinition font = new FontDefinition();
        font.setFamily(family);
        font.setFile(file);
        font.setWeight(weight);
        font.setStyle(style);
        font.setPrimary(primary);
        return font;
    }
}
