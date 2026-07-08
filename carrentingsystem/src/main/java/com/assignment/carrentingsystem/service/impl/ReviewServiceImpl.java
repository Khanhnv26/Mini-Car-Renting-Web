package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.CarRental;
import com.assignment.carrentingsystem.entity.Review;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.ReviewRepository;
import com.assignment.carrentingsystem.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CarRentalRepository carRentalRepository;

    @Override
    public Review save(ReviewDTO reviewDTO) {
        CarRental carRental = carRentalRepository.findById(reviewDTO.getCarRentalId())
                .orElseThrow(() -> new RuntimeException("Giao dịch thuê không tồn tại"));

        if (!"Completed".equals(carRental.getStatus())) {
            throw new RuntimeException("Chỉ đánh giá được giao dịch đã hoàn thành");
        }

        Review existingReview = reviewRepository.findByCarRental_CarRentID(reviewDTO.getCarRentalId());
        if (existingReview != null) {
            throw new RuntimeException("Giao dịch này đã được đánh giá");
        }

        Review review = new Review();
        review.setCarRental(carRental);
        review.setReviewStar(reviewDTO.getReviewStar());
        review.setComment(reviewDTO.getComment());
        return reviewRepository.save(review);
    }

    @Override
    public Review findByCarRentalId(Long carRentalId) {
        return reviewRepository.findByCarRental_CarRentID(carRentalId);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }
}
