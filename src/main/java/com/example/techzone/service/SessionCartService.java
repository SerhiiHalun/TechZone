package com.example.techzone.service;

import com.example.techzone.dto.CartItemDTO;
import com.example.techzone.model.*;
import com.example.techzone.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionCartService {
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CouponService couponService;


    public Page<CartItemDTO> getCartPage(HttpSession session, int page, int size) {
        List<CartItemDTO> cart = getSessionCart(session);

        int start = Math.min(page * size, cart.size());
        int end = Math.min((page + 1) * size, cart.size());
        List<CartItemDTO> subList = cart.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(subList, pageable, cart.size());
    }
    public double calculateTotal(List<CartItemDTO> cart) {
        return cart.stream()
                .mapToDouble(item -> {
                    double price = item.getProduct().getPrice();
                    long discount = item.getProduct().getDiscount();

                    if (discount > 0) {
                        price = price * (1 - discount / 100.0);
                        price = Math.round(price * 100.0) / 100.0;
                    }
                    return price * item.getAmount();
                })
                .sum();
    }

    @Transactional
    public Order createOrderFromSession(HttpSession session,String userEmail) {
        List<CartItemDTO> cartItems = getSessionCart(session);
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (cartItems.isEmpty()) throw new IllegalStateException("Cart is empty");
        if (user == null) throw new IllegalStateException("User not found in session");
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setCreationDate(LocalDateTime.now());
        order.setTotalAmount(calculateGrandTotal(session));

        Coupon coupon = (Coupon) session.getAttribute("appliedCoupon");
        if (coupon != null) {
            order.setCouponCode(coupon.getName());
        }

        List<OrderItem> orderItems = cartItems.stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(dto.getProduct());
            item.setAmount((long) dto.getAmount());
            item.setPriceAtPurchase(dto.getProduct().getPrice());
            return item;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        return orderRepository.save(order);
    }
    public void addToSessionCart(long productId, int amount, HttpSession session) {
        Product product = productService.getProductById(productId);
        List<CartItemDTO> cart = getSessionCart(session);

        Optional<CartItemDTO> existing = cart.stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst();

        int currentAmountInCart = existing.map(CartItemDTO::getAmount).orElse(0);
        int totalRequested = currentAmountInCart + amount;

        if (!productService.hasEnoughStock(product.getId(), totalRequested)) {
            throw new IllegalStateException("Sorry, only " + product.getAvailAmount() + " items left in stock.");
        }

        if (existing.isPresent()) {
            CartItemDTO item = existing.get();
            item.setAmount(Math.min(item.getAmount() + amount, 10));
        } else {
            cart.add(new CartItemDTO(product, Math.min(amount, 10)));
        }
        session.setAttribute("cart", cart);
    }
    public void updateAmount(long productId, int delta, HttpSession session) {
        List<CartItemDTO> cart = getSessionCart(session);

        Product product = productService.getProductById(productId);

        CartItemDTO targetItem = null;
        for (CartItemDTO item : cart) {
            if (item.getProduct().getId() == productId) {
                targetItem = item;
                break;
            }
        }
        if (targetItem != null) {
            int newAmount = targetItem.getAmount() + delta;
            if (newAmount <= 0) {
                cart.remove(targetItem);
            } else {
                if (delta > 0 && !productService.hasEnoughStock(product.getId(), newAmount)) {
                    return;
                }
                if (newAmount > 10) {
                    newAmount = 10;
                }
                    targetItem.setAmount(newAmount);
                    targetItem.setProduct(product);
                }
            }
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
    public void applyCoupon(String code, HttpSession session) {
        Coupon coupon = couponService.validateCoupon(code);
        session.setAttribute("appliedCoupon", coupon);
    }
    public void removeCoupon(HttpSession session) {
        session.removeAttribute("appliedCoupon");
    }
    public double calculateGrandTotal(HttpSession session) {
        List<CartItemDTO> cart = getSessionCart(session);
        double total = calculateTotal(cart);

        Coupon coupon = (Coupon) session.getAttribute("appliedCoupon");
        if (coupon != null) {
            double discountAmount = total * (coupon.getDiscount() / 100.0);
            total = total - discountAmount;
        }

        return Math.round(total * 100.0) / 100.0;
    }
}
