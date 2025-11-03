package com.example.techzone.controller.web;


import com.example.techzone.dto.CartItemDTO;
import com.example.techzone.service.CartService;
import com.example.techzone.service.ProductService;
import com.example.techzone.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private CartService cartService;
    private ProductService productService;
    private UserService userservice;

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") int productId,
                            @RequestParam("amount") int amount,
                            HttpSession session) {
        cartService.addToSessionCart(productId, amount, session);
        return "redirect:/cart/list";
    }

    @GetMapping("/list")
    public String showCart(@RequestParam(defaultValue = "0") int page,Model model, HttpSession session, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        int pageSize = 10;
        Page<CartItemDTO> cartPage = cartService.getCartPage(session, page, pageSize);
        model.addAttribute("cartPage", cartPage);
        model.addAttribute("totalPrice", cartService.calculateTotal(cartService.getSessionCart(session)));

        return "/product/cart";

    }
    @PostMapping("/increase")
    public String increaseAmount(@RequestParam int productId, HttpSession session) {
        cartService.updateAmount(productId, 1, session);
        return "redirect:/cart/list";
    }


    @PostMapping("/decrease")
    public String decreaseAmount(@RequestParam int productId, HttpSession session) {
        cartService.updateAmount(productId, -1, session);
        return "redirect:/cart/list";
    }

    @PostMapping("/payment")
    public String proceedToPayment(HttpSession session, Model model) {
        List<CartItemDTO> cart = cartService.getSessionCart(session);
        double total = cartService.calculateTotal(cart);

        if (cart.isEmpty()) {
            model.addAttribute("message", "Your cart is empty!");
            return "redirect:/cart/list";
        }

        model.addAttribute("totalPrice", total);
        return "payment";
    }

}
