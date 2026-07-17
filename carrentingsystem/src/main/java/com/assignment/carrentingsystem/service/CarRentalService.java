package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.RentalReportDTO;
import com.assignment.carrentingsystem.dto.RentalRequest;
import com.assignment.carrentingsystem.entity.CarRental;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface CarRentalService {
    void createCarRental(Long customerId, RentalRequest request);
    List<CarRental> findAll();
    List<CarRental> findByCustomerId(Long customerId);
    CarRental findById(Long id);
    void updateStatus(Long rentalId, String status);
    List<CarRental> findByPickUpDateBetween(LocalDateTime start, LocalDateTime end);
    List<RentalReportDTO> findRentalReportByPickUpDateBetween(LocalDateTime start, LocalDateTime end);
    Page<CarRental> findRentalsPaginated(Long customerId, String status, LocalDateTime start, LocalDateTime end, int page, int size, String sortBy, String sortDir);
}
