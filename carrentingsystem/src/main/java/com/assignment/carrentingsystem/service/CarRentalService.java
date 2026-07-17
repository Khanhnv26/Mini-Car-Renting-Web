package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.RentalReportDTO;
import com.assignment.carrentingsystem.dto.RentalRequest;
import com.assignment.carrentingsystem.entity.CarRental;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CarRentalService {
    void createCarRental(Long customerId, RentalRequest request);
    List<CarRental> findAll();
    List<CarRental> findByCustomerId(Long customerId);
    CarRental findById(Long id);
    void updateStatus(Long rentalId, String status);
    List<CarRental> findByPickUpDateBetween(LocalDateTime start, LocalDateTime end);
    List<RentalReportDTO> findRentalReportByPickUpDateBetween(LocalDateTime start, LocalDateTime end);
    Page<RentalReportDTO> findRentalReportPaginated(LocalDateTime start, LocalDateTime end, String status, String keyword, int page, int size);
    BigDecimal sumRentPriceFiltered(LocalDateTime start, LocalDateTime end, String status, String keyword);
    Map<String, Long> countByStatusFiltered(LocalDateTime start, LocalDateTime end, String status, String keyword);
    Page<CarRental> findRentalsPaginated(Long customerId, String status, LocalDateTime start, LocalDateTime end, int page, int size, String sortBy, String sortDir);
}
