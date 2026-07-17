package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.Review;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review save(ReviewDTO review);

    Review save(ReviewDTO review, Long customerId);

    Optional<Review> findByCarRentalId(Long carRentalId);

    boolean existsByCarRentalId(Long carRentalId);

    List<Review> findAll();

    Page<Review> findAllPaginated(int page, int size);
}
