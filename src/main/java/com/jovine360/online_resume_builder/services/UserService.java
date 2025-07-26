// UserService.java
package com.jovine360.online_resume_builder.services;

import com.jovine360.online_resume_builder.models.User;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    User updateUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
