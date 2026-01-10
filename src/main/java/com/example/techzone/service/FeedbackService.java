package com.example.techzone.service;


import com.example.techzone.dto.FeedbackCreateDto;
import com.example.techzone.dto.FeedbackResponseDto;
import com.example.techzone.exception.NotFoundException;
import com.example.techzone.mapper.FeedbackMapper;
import com.example.techzone.model.Feedback;
import com.example.techzone.model.Product;
import com.example.techzone.model.User;
import com.example.techzone.repository.FeedbackRepository;
import com.example.techzone.repository.ProductRepository;
import com.example.techzone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FeedbackService {
    private final ProductRepository productRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    private final FeedbackMapper mapper;
    @Autowired
    public FeedbackService(ProductRepository productRepository, FeedbackRepository feedbackRepository, UserRepository userRepository, FeedbackMapper mapper) {
        this.productRepository = productRepository;
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    public FeedbackResponseDto addFeedback(Long productId, FeedbackCreateDto dto, String userEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User with email " + userEmail + " not found"));
        Feedback fb = mapper.toEntity(dto, product, user);
        feedbackRepository.save(fb);
        return mapper.toDto(fb);
    }


    @Transactional(readOnly = true)
    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Feedback with id " + id + " not found"));
    }


    @Transactional(readOnly = true)
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksByProductId(long productId) {
        return feedbackRepository.findAllByProductId(productId)
                .stream().map(FeedbackMapper::toDto).toList();

    }

    @Transactional(readOnly = true)
    public List<Feedback> getFeedbacksByUserId(long userId) {
        return feedbackRepository.findAllByUserId(userId);
    }

    @Transactional
    public Feedback updateFeedback(Long id, Feedback updatedFeedback) {

        Feedback existing = getFeedbackById(id);

        existing.setFeedback(updatedFeedback.getFeedback());
        existing.setProduct(updatedFeedback.getProduct());
        existing.setUser(updatedFeedback.getUser());

        return feedbackRepository.save(existing);
    }


    @Transactional
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new NoSuchElementException("Feedback with id " + id + " not found");
        }
        feedbackRepository.deleteById(id);
    }

}
