package com.example.techzone.service;


import com.example.techzone.dto.CartItemDTO;
import com.example.techzone.model.Cart;
import com.example.techzone.model.Product;
import com.example.techzone.model.User;
import com.example.techzone.repository.CartRepository;
import com.example.techzone.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private HttpSession session;

    public CartService(CartRepository cartRepository, HttpSession session, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.session = session;
        this.productRepository = productRepository;
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }
    public Page<CartItemDTO> getCartPage(HttpSession session, int page, int size) {
        List<CartItemDTO> cart = getSessionCart(session);

        int start = Math.min(page * size, cart.size());
        int end = Math.min((page + 1) * size, cart.size());
        List<CartItemDTO> subList = cart.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(subList, pageable, cart.size());
    }
    public Cart getCartById(int id) {
        return cartRepository.findById(id).orElse(null);
    }
    public List<Cart> getCartByUser(User user) {
        return cartRepository.findAllCartsByUser(user);
    }
    public Cart createCart(User user, Product product, Long amount) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setAmount(amount);
        return cartRepository.save(cart);
    }
    public Cart updateCart(int id, User user, Product product, Long amount) {

        return cartRepository.findById(id).map(cart -> {
            if (user != null) {
                cart.setUser(user);
            }
            if (product != null) {
                cart.setProduct(product);
            }
            if (amount != null) {
                cart.setAmount(amount);
            }
            return cartRepository.save(cart);
        }).orElseThrow(() -> new RuntimeException("Cart not found"));
    }
    public void addToSessionCart(int productId, int amount, HttpSession session) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getImages() != null) {
            product.getImages().size();
        }

        @SuppressWarnings("unchecked")
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        Optional<CartItemDTO> existing = cart.stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst();

        if (existing.isPresent()) {
            CartItemDTO item = existing.get();
            int newAmount = Math.min(item.getAmount() + amount, 10);
            item.setAmount(newAmount);
        } else {
            cart.add(new CartItemDTO(product, Math.min(amount, 10)));
        }

        session.setAttribute("cart", cart);
    }
    public void updateAmount(int productId, int delta, HttpSession session) {
        List<CartItemDTO> cart = getSessionCart(session);
        cart.forEach(item -> {
            if (item.getProduct().getId() == productId) {
                int newAmount = Math.min(Math.max(item.getAmount() + delta, 1), 10);
                item.setAmount(newAmount);
            }
        });
        session.setAttribute("cart", cart);
    }
    public List<CartItemDTO> getSessionCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
    public double calculateTotal(List<CartItemDTO> cart) {
        return cart.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getAmount())
                .sum();
    }
    public boolean exceedsLimit(List<CartItemDTO> cart) {
        return cart.stream().anyMatch(item -> item.getAmount() >= 10);
    }

    public void deleteCart(int id) {
        cartRepository.deleteById(id);
    }


}