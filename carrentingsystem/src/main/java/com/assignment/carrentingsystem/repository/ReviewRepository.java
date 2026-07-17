package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByCarRental_CarRentID(Long carRentId);

    boolean existsByCarRental_CarRentID(Long carRentId);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    Page<Review> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    Page<Review> findByCarRental_Customer_CustomerId(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    List<Review> findByCarRental_CarRentIDIn(Collection<Long> carRentIds);
}
