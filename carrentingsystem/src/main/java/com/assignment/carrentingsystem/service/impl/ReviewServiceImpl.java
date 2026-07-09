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

    private Review toEntity(ReviewDTO dto) {
        CarRental carRental = carRentalRepository.findById(dto.getCarRentalId())
                .orElseThrow(() -> new RuntimeException("Giao dịch thuê không tồn tại"));
        Review review = new Review();
        review.setCarRental(carRental);
        review.setReviewStar(dto.getReviewStar());
        review.setComment(dto.getComment());
        return review;
    }

    @Override
    public Review save(ReviewDTO reviewDTO) {
        CarRental carRental = carRentalRepository.findById(reviewDTO.getCarRentalId())
                .orElseThrow(() -> new RuntimeException("Giao dịch thuê không tồn tại"));
        if (!"Completed".equals(carRental.getStatus())) {
            throw new RuntimeException("Chỉ đánh giá được giao dịch đã hoàn thành");
        }
        Review existing = reviewRepository.findByCarRental_CarRentID(reviewDTO.getCarRentalId());
        if (existing != null) {
            throw new RuntimeException("Giao dịch này đã được đánh giá");
        }
        return reviewRepository.save(toEntity(reviewDTO));
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
