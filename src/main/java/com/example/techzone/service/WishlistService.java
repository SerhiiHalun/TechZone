package com.example.techzone.service;

import com.example.techzone.model.Product;
import com.example.techzone.model.User;
import com.example.techzone.model.Wishlist;
import com.example.techzone.repository.WishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }
    public List<Wishlist> getAllWishlists() {
        return wishlistRepository.findAll();
    }
    public Wishlist getWishlistById(long id) {
        return wishlistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));
    }
    public Wishlist createWishlist(User user, Product product) {
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        return wishlistRepository.save(wishlist);
    }
    public Wishlist updateWishlist(long id, User user, Product product) {
        Wishlist wishlist = getWishlistById(id);
        wishlist.setUser(user);
        wishlist.setProduct(product);
        return wishlistRepository.save(wishlist);
    }
    public void deleteWishlist(long id) {
        wishlistRepository.deleteById(id);
    }
}
