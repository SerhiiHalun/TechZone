package com.example.techzone.controller.web;


import com.example.techzone.model.Order;

import com.example.techzone.service.PaymentService;
import com.example.techzone.service.SessionCartService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final SessionCartService sessionCartService;
    private final PaymentService paymentService;

    @Value("${stripe.publishable.key}")
    private String stripePublicKey;

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                throw new IllegalStateException("You must be logged in");
            }
            Order order = sessionCartService.createOrderFromSession(session, principal.getName());

            String clientSecret = paymentService.createPaymentIntent(order);

            model.addAttribute("clientSecret", clientSecret);
            model.addAttribute("orderId", order.getId());
            model.addAttribute("totalAmount", order.getTotalAmount());
            model.addAttribute("stripePublicKey", stripePublicKey);

            return "/product/payment";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart/list";
        }
    }

    @GetMapping("/success")
    public String success(@RequestParam Long orderId,HttpSession session) {
        paymentService.updatePaymentStatus(orderId, true);
        session.removeAttribute("cart");
        return "redirect:/cart/success";
    }

    @GetMapping("/failed")
    public String failed(@RequestParam Long orderId) {
        paymentService.updatePaymentStatus(orderId, false);
        return "redirect:/cart/failed";
    }
}
