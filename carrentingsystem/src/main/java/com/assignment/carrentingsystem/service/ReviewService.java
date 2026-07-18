package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.Review;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewService {
    Review save(ReviewDTO review);

    Review save(ReviewDTO review, Integer customerId);

    Optional<Review> findByCarRentalId(Integer carRentalId);

    boolean existsByCarRentalId(Integer carRentalId);

    List<Review> findAll();

    Page<Review> findAllPaginated(int page, int size);

    Page<Review> findByCustomerIdPaginated(Integer customerId, int page, int size);

    Page<Review> findAllFiltered(Integer star, String keyword, int page, int size);

    Page<Review> findByCustomerFiltered(Integer customerId, Integer star, String keyword, int page, int size);

    Map<Integer, Review> findByCarRentalIds(Collection<Integer> carRentalIds);

    List<Review> findByCarId(Integer carId);
}
