# Resume Template Fonts

This directory contains custom fonts for different resume templates.

## Font Organization

### Creative Template Fonts
- **OpenSans-Regular.ttf** - Clean, modern font for body text
- **Montserrat-Bold.ttf** - Strong, geometric font for headings
- **Poppins-Light.ttf** - Light, friendly font for accent text

### Modern Template Fonts
- **Roboto-Regular.ttf** - Google's modern sans-serif
- **Roboto-Bold.ttf** - Bold variant for headings

### Professional Template Fonts
- **SourceSerif4-Regular.ttf** - Elegant serif font for professional documents
- **SourceSerif4-Bold.ttf** - Bold variant for headings

### Minimal Template Fonts
- **Inter-Regular.ttf** - Clean, minimal font optimized for UI
- **Inter-Medium.ttf** - Medium weight for emphasis

### Default Template Fonts
- **Lato-Regular.ttf** - Friendly, professional sans-serif
- **Lato-Bold.ttf** - Bold variant for headings

## Font Licensing

Please ensure all fonts used comply with their respective licenses:
- Google Fonts (Open Sans, Roboto, Inter, Lato) - SIL Open Font License
- Adobe Fonts (Source Serif) - SIL Open Font License
- Other fonts - Check individual licenses

## Adding New Fonts

1. Place font files (.ttf) in this directory
2. Update the corresponding setup method in PDFService.java
3. Test with the template to ensure proper rendering
4. Update this README with font information

## Fallback Strategy

If custom fonts are not available, the system will fall back to:
1. System fonts (Helvetica, Times-Roman, Courier)
2. Browser default fonts
3. Flying Saucer built-in fonts
