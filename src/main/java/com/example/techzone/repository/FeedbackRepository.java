package com.example.techzone.repository;


import com.example.techzone.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    List<Feedback> findAllByProductId(long productId);
    List<Feedback> findAllByUserId(long userId);
}
