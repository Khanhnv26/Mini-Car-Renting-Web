package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.CarRental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @EntityGraph(attributePaths = {"customer", "car"})
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

    @EntityGraph(attributePaths = {"customer", "car"})
    @Query("SELECT cr FROM CarRental cr WHERE " +
           "(:start IS NULL OR cr.pickUpDate >= :start) AND " +
           "(:end IS NULL OR cr.pickUpDate <= :end) AND " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(cr.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CarRental> findReportFiltered(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("status") String status,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);

    @Query("SELECT COALESCE(SUM(cr.rentPrice), 0) FROM CarRental cr WHERE " +
           "(:start IS NULL OR cr.pickUpDate >= :start) AND " +
           "(:end IS NULL OR cr.pickUpDate <= :end) AND " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(cr.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    BigDecimal sumRentPriceFiltered(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("status") String status,
                                    @Param("keyword") String keyword);

    @Query("SELECT cr.status, COUNT(cr) FROM CarRental cr WHERE " +
           "(:start IS NULL OR cr.pickUpDate >= :start) AND " +
           "(:end IS NULL OR cr.pickUpDate <= :end) AND " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(cr.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "GROUP BY cr.status")
    List<Object[]> countGroupByStatusFiltered(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("status") String status,
                                              @Param("keyword") String keyword);

    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM CarRental cr " +
           "WHERE cr.car.carId = :carId " +
           "AND cr.status IN ('Pending', 'Renting') " +
           "AND cr.pickUpDate < :returnDate AND cr.returnDate > :pickupDate")
    boolean existsOverlappingRental(@Param("carId") Long carId,
                                    @Param("pickupDate") LocalDateTime pickupDate,
                                    @Param("returnDate") LocalDateTime returnDate);

    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM CarRental cr " +
           "WHERE cr.car.carId = :carId " +
           "AND cr.carRentID <> :excludeRentalId " +
           "AND cr.status IN ('Pending', 'Renting')")
    boolean existsOtherActiveRental(@Param("carId") Long carId,
                                    @Param("excludeRentalId") Long excludeRentalId);
}
