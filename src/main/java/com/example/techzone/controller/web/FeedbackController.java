package com.example.techzone.controller.web;

import com.example.techzone.dto.FeedbackCreateDto;
import com.example.techzone.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;


@Controller
@RequestMapping("/products/{productId}")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;


    @PostMapping("/review")
    public String addReview(@ModelAttribute("review") @Valid FeedbackCreateDto dto,
                            @PathVariable Long productId,
                            Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        feedbackService.addFeedback(productId, dto, principal.getName());
        return "redirect:/product/" + productId;
    }
}
