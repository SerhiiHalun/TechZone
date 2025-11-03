package com.example.techzone.mapper;

import lombok.AllArgsConstructor;
import com.example.techzone.dto.FeedbackCreateDto;
import com.example.techzone.dto.FeedbackResponseDto;
import com.example.techzone.model.Feedback;
import com.example.techzone.model.Product;
import com.example.techzone.model.User;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FeedbackMapper {

    public static FeedbackResponseDto toDto(Feedback feedback) {
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setId(feedback.getId());
        dto.setFeedback(feedback.getFeedback());
        dto.setRating(feedback.getRating());
        dto.setUsername(feedback.getUser().getFirstName());
        return dto;
    }

    public static Feedback toEntity(FeedbackCreateDto dto, Product product, User user) {
        Feedback feedback = new Feedback();
        feedback.setFeedback(dto.getFeedback());
        feedback.setRating(dto.getRating());
        feedback.setProduct(product);
        feedback.setUser(user);
        return feedback;
    }
}
