package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.RentalReportDTO;
import com.assignment.carrentingsystem.dto.RentalRequest;
import com.assignment.carrentingsystem.entity.CarRental;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CarRentalService {
    void createCarRental(Integer customerId, RentalRequest request);
    List<CarRental> findAll();
    List<CarRental> findByCustomerId(Integer customerId);
    CarRental findById(Integer id);
    void updateStatus(Integer rentalId, String status);
    List<CarRental> findByPickupDateBetween(LocalDate start, LocalDate end);
    List<RentalReportDTO> findRentalReportByPickupDateBetween(LocalDate start, LocalDate end);
    Page<RentalReportDTO> findRentalReportPaginated(LocalDate start, LocalDate end, String status, String keyword, int page, int size);
    BigDecimal sumRentPriceFiltered(LocalDate start, LocalDate end, String status, String keyword);
    Map<String, Long> countByStatusFiltered(LocalDate start, LocalDate end, String status, String keyword);
    Page<CarRental> findRentalsPaginated(Integer customerId, String status, LocalDate start, LocalDate end, String keyword, int page, int size, String sortBy, String sortDir);
}
