package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByStatus(String status);

    List<Car> findByCarNameContainingIgnoreCase(String carName);

    boolean existsByCarProducer_ProducerId(Long producerId);
}
