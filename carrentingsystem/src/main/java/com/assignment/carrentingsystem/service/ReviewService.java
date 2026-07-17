package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review save(ReviewDTO review);
    Optional<Review> findByCarRentalId(Long carRentalId);
    List<Review> findAll();
}
