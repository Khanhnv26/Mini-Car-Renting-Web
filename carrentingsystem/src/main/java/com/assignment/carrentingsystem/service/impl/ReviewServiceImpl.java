package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.CarRental;
import com.assignment.carrentingsystem.entity.Review;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.ReviewRepository;
import com.assignment.carrentingsystem.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CarRentalRepository carRentalRepository;

    private Review toEntity(ReviewDTO dto, CarRental carRental) {
        Review review = new Review();
        review.setCarRental(carRental);
        review.setReviewStar(dto.getReviewStar());
        review.setComment(dto.getComment());
        return review;
    }

    @Override
    @Transactional
    public Review save(ReviewDTO reviewDTO) {
        return save(reviewDTO, null);
    }

    @Override
    @Transactional
    public Review save(ReviewDTO reviewDTO, Long customerId) {
        CarRental carRental = carRentalRepository.findById(reviewDTO.getCarRentalId())
                .orElseThrow(() -> new RuntimeException("Giao dịch thuê không tồn tại"));
        if (customerId != null
                && (carRental.getCustomer() == null
                || !customerId.equals(carRental.getCustomer().getCustomerId()))) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá giao dịch của chính mình");
        }
        if (!"Completed".equals(carRental.getStatus())) {
            throw new RuntimeException("Chỉ đánh giá được giao dịch đã hoàn thành");
        }
        if (reviewRepository.existsByCarRental_CarRentID(reviewDTO.getCarRentalId())) {
            throw new RuntimeException("Giao dịch này đã được đánh giá");
        }
        return reviewRepository.save(toEntity(reviewDTO, carRental));
    }

    @Override
    public Optional<Review> findByCarRentalId(Long carRentalId) {
        return reviewRepository.findByCarRental_CarRentID(carRentalId);
    }

    @Override
    public boolean existsByCarRentalId(Long carRentalId) {
        return reviewRepository.existsByCarRental_CarRentID(carRentalId);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Page<Review> findAllPaginated(int page, int size) {
        return reviewRepository.findAllBy(PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @Override
    public Page<Review> findByCustomerIdPaginated(Long customerId, int page, int size) {
        return reviewRepository.findByCarRental_Customer_CustomerId(
                customerId, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @Override
    public Map<Long, Review> findByCarRentalIds(Collection<Long> carRentalIds) {
        if (carRentalIds == null || carRentalIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return reviewRepository.findByCarRental_CarRentIDIn(carRentalIds).stream()
                .filter(r -> r.getCarRental() != null && r.getCarRental().getCarRentID() != null)
                .collect(Collectors.toMap(
                        r -> r.getCarRental().getCarRentID(),
                        Function.identity(),
                        (a, b) -> a));
    }
}
