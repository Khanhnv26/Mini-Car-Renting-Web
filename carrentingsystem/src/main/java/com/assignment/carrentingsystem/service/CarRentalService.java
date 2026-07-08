package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.CarRentalDTO;
import com.assignment.carrentingsystem.entity.CarRental;

import java.time.LocalDateTime;
import java.util.List;

public interface CarRentalService {
    void createCarRental(Long customerId, CarRentalDTO carRentalDTO);
    List<CarRental> findAll();
    List<CarRental> findByCustomerId(Long customerId);
    CarRental findById(Long id);
    void updateStatus(Long rentalId, String status);
    List<CarRental> findByPickUpDateBetween(LocalDateTime start, LocalDateTime end);
}
