package com.example.techzone.service;


import com.example.techzone.model.Role;
import com.example.techzone.model.User;
import com.example.techzone.repository.UserRepository;
import com.example.techzone.security.JwtTokenProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, @Lazy JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }
    public String loginUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return jwtTokenProvider.generateToken(user.getEmail(), List.of(Role.USER.name()));

    }
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("No authenticated user found.");
        }
        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            throw new IllegalArgumentException("Unexpected authentication principal.");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(RuntimeException::new);
    }
    public User updateUser(String firstName, String lastName, String email, String password, String role) {
        User user = getCurrentUser();
            if (firstName != null && !firstName.isBlank()) {
                user.setFirstName(firstName);
            }
            if (lastName != null && !lastName.isBlank()) {
                user.setLastName(lastName);
            }
            if (email != null && !email.isBlank()) {
                user.setEmail(email);
            }
            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            if (role != null) {
                try {
                    user.setRole(Role.valueOf(role.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid role: " + role);
                }
            }
            return userRepository.save(user);
    }
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
