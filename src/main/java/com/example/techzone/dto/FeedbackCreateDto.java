package com.example.techzone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackCreateDto {

    @NotBlank
    private String feedback;

    @Min(1)
    @Max(5)
    private int rating;
}