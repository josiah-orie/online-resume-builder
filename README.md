# Online Resume Builder

A modern, web-based resume builder application that allows users to create professional resumes with multiple templates and export them as high-quality PDF documents.

## 🚀 Features

### User Management & Authentication
- **User Registration & Login** - Secure user authentication with Spring Security
- **User Profiles** - Personal information management and account settings
- **Session Management** - Secure user sessions with proper logout functionality

### Resume Creation & Management
- **Interactive Resume Builder** - Step-by-step resume creation process
- **Multiple Resume Support** - Create and manage multiple resumes per user
- **Real-time Preview** - Live preview of resume changes during editing
- **Auto-save** - Automatic saving of resume progress
- **Resume Templates** - Choose from 5 professionally designed templates

### Professional Templates
- **Default Template** - Clean, balanced design suitable for most industries
- **Modern Template** - Contemporary layout with gradient headers and timeline styling
- **Professional Template** - Traditional serif fonts with formal corporate styling
- **Creative Template** - Colorful sidebar design with skill bars for creative professionals
- **Minimal Template** - Clean, minimalist approach with subtle styling

### Resume Sections
- **Personal Information** - Contact details and professional summary
- **Work Experience** - Job history with descriptions and project details
- **Education** - Academic background and qualifications
- **Skills** - Technical and soft skills with proficiency levels
- **Certifications** - Professional certifications and credentials
- **Languages** - Language proficiencies
- **Projects** - Portfolio projects with descriptions and technologies used
- **Hobbies & Interests** - Personal interests and activities

### PDF Generation
- **High-Quality PDFs** - Professional PDF generation using Flying Saucer library
- **Template-Specific Styling** - Unique PDF layouts for each template
- **Custom Typography** - Template-specific fonts for enhanced visual appeal
- **Print-Ready Format** - A4 format optimized for printing
- **Instant Download** - Generate and download PDFs on demand

### Advanced Features
- **Custom Font Management** - Template-specific font loading with fallback support
- **Responsive Design** - Mobile-friendly interface
- **Data Validation** - Client and server-side form validation
- **Error Handling** - Comprehensive error handling and user feedback
- **Security** - Protection against common web vulnerabilities

## 🛠️ Technology Stack

### Backend
- **Java 17** - Modern Java features and performance
- **Spring Boot 3.5.3** - Enterprise application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations and ORM
- **Thymeleaf** - Server-side template engine
- **MySQL** - Relational database management
- **Maven** - Dependency management and build tool

### Frontend
- **Thymeleaf Templates** - Dynamic HTML generation
- **Bootstrap/CSS3** - Responsive styling and layout
- **JavaScript** - Interactive user interface elements
- **Font Integration** - Custom web fonts for enhanced typography

### PDF Generation
- **Flying Saucer (iText)** - HTML to PDF conversion
- **Custom CSS** - Template-specific styling
- **Font Management** - Typography optimization for print

## 🎨 Template Gallery

### Default Template
Professional layout with clean typography, perfect for traditional industries and general use.

### Modern Template
Features gradient headers, timeline-style experience sections, and contemporary color schemes.

### Professional Template
Elegant serif typography with formal styling, ideal for executive and corporate positions.

### Creative Template
Vibrant sidebar design with skill visualization bars, perfect for designers and creative professionals.

### Minimal Template
Clean, minimalist approach with ample white space and subtle styling for modern professionals.

## 📋 Requirements

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Modern web browser

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/josiah-orie/online-resume-builder.git
   cd online-resume-builder
   ```

2. **Configure Database**
   - Create a MySQL database
   - Update `application.properties` with your database credentials

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access Application**
   - Open your browser to `http://localhost:8080`
   - Register a new account or login
   - Start building your resume!

## 📊 Project Structure
