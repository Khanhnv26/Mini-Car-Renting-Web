package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Optional<Review> findByCarRental_CarRenId(Integer carRenId);

    boolean existsByCarRental_CarRenId(Integer carRenId);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    Page<Review> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    Page<Review> findByCarRental_Customer_CustomerId(Integer customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    List<Review> findByCarRental_CarRenIdIn(Collection<Integer> carRenIds);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    List<Review> findByCarRental_Car_CarIdOrderByIdDesc(Integer carId);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    @Query("SELECT r FROM Review r WHERE " +
           "(:star IS NULL OR r.reviewStar = :star) AND " +
           "(:keyword IS NULL OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.carRental.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Review> findFiltered(@Param("star") Integer star,
                              @Param("keyword") String keyword,
                              Pageable pageable);

    @EntityGraph(attributePaths = {"carRental", "carRental.car", "carRental.customer"})
    @Query("SELECT r FROM Review r WHERE " +
           "r.carRental.customer.customerId = :customerId AND " +
           "(:star IS NULL OR r.reviewStar = :star) AND " +
           "(:keyword IS NULL OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.carRental.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Review> findFilteredByCustomer(@Param("customerId") Integer customerId,
                                        @Param("star") Integer star,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);
}
