package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByCarRental_CarRentID(Long carRentId);
}
