package com.jovine360.online_resume_builder.controllers;

import com.jovine360.online_resume_builder.models.User;
import com.jovine360.online_resume_builder.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String welcome() {
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       @RequestParam(required = false) String registered,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        
        if (registered != null) {
            model.addAttribute("message", "Registration successful! You can now login with your credentials");
        }
        
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult result,
                              @RequestParam("confirmPassword") String confirmPassword,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        
        // Custom validation for password confirmation
        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.user", "Passwords do not match");
        }
        
        // Check for password strength
        if (user.getPassword().length() < 8) {
            result.rejectValue("password", "error.user", 
                    "Password must be at least 8 characters long");
        }
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        try {
            // Check if username or email already exists
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Username already exists. Please choose a different one.");
                return "auth/register";
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already exists. Please use a different email address.");
                return "auth/register";
            }
            
            // Register the user
            userService.registerUser(user);
            
            // Redirect to login page with success message
            redirectAttributes.addAttribute("registered", "true");
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during registration: " + e.getMessage());
            return "auth/register";
        }
    }
    
    @PostMapping("/manual-login")
    public String manualLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        
        try {
            // Create authentication token
            UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, password);
            
            // Add details from the request
            authToken.setDetails(new WebAuthenticationDetails(request));
            
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Redirect to dashboard
            return "redirect:/dashboard";
            
        } catch (AuthenticationException e) {
            // Authentication failed
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}