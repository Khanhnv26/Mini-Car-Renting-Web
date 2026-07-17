package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByStatus(String status);

    List<Car> findByCarNameContainingIgnoreCase(String carName);

    boolean existsByCarProducer_ProducerId(Long producerId);

    @Query("SELECT c FROM Car c WHERE " +
           "(:name IS NULL OR LOWER(c.carName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:producerId IS NULL OR c.carProducer.producerId = :producerId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:minPrice IS NULL OR c.rentPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR c.rentPrice <= :maxPrice)")
    Page<Car> findCarsWithFilters(@Param("name") String name,
                                 @Param("producerId") Long producerId,
                                 @Param("status") String status,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);
}
