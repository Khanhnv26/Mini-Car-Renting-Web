package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.CarRental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarRentalRepository extends JpaRepository<CarRental, Long> {
    @Query("from CarRental cr where cr.customer.customerId =:id")
    List<CarRental> findByCustomerId(@Param("id") Long customerId);
    boolean existsByCar_CarId(Long carId);
    boolean existsByCustomer_CustomerId(Long customerId);
    @Query("from CarRental cr where cr.pickUpDate between :start and :end order by cr.rentPrice desc")
    List<CarRental> findCarRentalByPickUpDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT cr FROM CarRental cr WHERE " +
           "(:customerId IS NULL OR cr.customer.customerId = :customerId) AND " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:start IS NULL OR cr.pickUpDate >= :start) AND " +
           "(:end IS NULL OR cr.pickUpDate <= :end)")
    Page<CarRental> findRentalsWithFilters(@Param("customerId") Long customerId,
                                          @Param("status") String status,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          Pageable pageable);
}
