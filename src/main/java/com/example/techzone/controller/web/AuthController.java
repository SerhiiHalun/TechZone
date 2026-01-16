package com.example.techzone.controller.web;


import com.example.techzone.model.User;
import com.example.techzone.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String showLoginForm() {

        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user,
            RedirectAttributes redirectAttributes
    ) {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already registered");
            return "redirect:/auth/register";
        }
        userService.registerUser(user);

        autoLogin(user.getEmail(), user.getPassword());

        return "redirect:/auth/account";
    }

    private void autoLogin(String email, String rawPassword) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, rawPassword);

        var authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

