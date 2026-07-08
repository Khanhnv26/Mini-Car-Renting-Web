package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review findByCarRental_CarRentID(Long reviewId);
}
