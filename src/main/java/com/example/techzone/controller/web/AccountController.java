package com.example.techzone.controller.web;


import com.example.techzone.model.User;
import com.example.techzone.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/auth")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public String accountPage(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/auth/login";
        }

        String email = principal.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        model.addAttribute("user", user);
        return "auth/account";
    }
}

