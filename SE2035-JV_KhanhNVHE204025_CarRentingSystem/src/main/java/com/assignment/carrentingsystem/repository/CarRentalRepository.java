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
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarRentalRepository extends JpaRepository<CarRental, Integer> {

    @Query("from CarRental cr where cr.customer.customerId = :id")
    List<CarRental> findByCustomerId(@Param("id") Integer customerId);

    boolean existsByCar_CarId(Integer carId);

    boolean existsByCustomer_CustomerId(Integer customerId);

    @Query("from CarRental cr where cr.pickupDate between :start and :end order by cr.rentPrice desc")
    List<CarRental> findCarRentalByPickupDate(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @EntityGraph(attributePaths = {"customer", "car"})
    @Query("SELECT cr FROM CarRental cr WHERE " +
           "(:customerId IS NULL OR cr.customer.customerId = :customerId) AND " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:start IS NULL OR cr.pickupDate >= :start) AND " +
           "(:end IS NULL OR cr.pickupDate <= :end) AND " +
           "(:keyword IS NULL OR LOWER(cr.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CarRental> findRentalsWithFilters(@Param("customerId") Integer customerId,
                                           @Param("status") String status,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "car"})
    @Query("SELECT cr FROM CarRental cr WHERE " +
           "(:start IS NULL OR cr.pickupDate >= :start) AND " +
           "(:end IS NULL OR cr.pickupDate <= :end) AND " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(cr.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<CarRental> findReportFiltered(@Param("start") LocalDate start,
                                       @Param("end") LocalDate end,
                                       @Param("status") String status,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);

    @Query("SELECT COALESCE(SUM(cr.rentPrice), 0) FROM CarRental cr WHERE " +
           "(:start IS NULL OR cr.pickupDate >= :start) AND " +
           "(:end IS NULL OR cr.pickupDate <= :end) AND " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(cr.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    BigDecimal sumRentPriceFiltered(@Param("start") LocalDate start,
                                    @Param("end") LocalDate end,
                                    @Param("status") String status,
                                    @Param("keyword") String keyword);

    @Query("SELECT cr.status, COUNT(cr) FROM CarRental cr WHERE " +
           "(:start IS NULL OR cr.pickupDate >= :start) AND " +
           "(:end IS NULL OR cr.pickupDate <= :end) AND " +
           "(:status IS NULL OR cr.status = :status) AND " +
           "(:keyword IS NULL OR LOWER(cr.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(cr.car.carName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "GROUP BY cr.status")
    List<Object[]> countGroupByStatusFiltered(@Param("start") LocalDate start,
                                              @Param("end") LocalDate end,
                                              @Param("status") String status,
                                              @Param("keyword") String keyword);

    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM CarRental cr " +
           "WHERE cr.car.carId = :carId " +
           "AND cr.status IN ('Pending', 'Renting') " +
           "AND cr.pickupDate < :returnDate AND cr.returnDate > :pickupDate")
    boolean existsOverlappingRental(@Param("carId") Integer carId,
                                    @Param("pickupDate") LocalDate pickupDate,
                                    @Param("returnDate") LocalDate returnDate);

    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM CarRental cr " +
           "WHERE cr.car.carId = :carId " +
           "AND cr.carRenId <> :excludeRentalId " +
           "AND cr.status IN ('Pending', 'Renting')")
    boolean existsOtherActiveRental(@Param("carId") Integer carId,
                                    @Param("excludeRentalId") Integer excludeRentalId);
}
