package com.example.techzone.controller.api;


import com.example.techzone.model.User;
import com.example.techzone.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;


    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody User user) {
        User returnUser = userService.registerUser(user);
        return returnUser != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(user)
                : ResponseEntity.badRequest().body("Registration failed");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String email, @RequestParam String password) {
        String token = userService.loginUser(email, password);
        return token != null
                ? ResponseEntity.ok(token)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getUser() {
        User user = userService.getCurrentUser();
        return user != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(users);
    }


    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return user != null
                ? ResponseEntity.ok(user)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String role) {
        User updatedUser = userService.updateUser(firstName, lastName, email, password, role);
        return updatedUser != null
                ? ResponseEntity.ok(updatedUser)
                : ResponseEntity.badRequest().body("Update failed");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }
}
