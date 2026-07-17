package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.RentalReportDTO;
import com.assignment.carrentingsystem.dto.RentalRequest;
import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.entity.CarRental;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.CarRepository;
import com.assignment.carrentingsystem.repository.CustomerRepository;
import com.assignment.carrentingsystem.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarRentalServiceImpl implements CarRentalService {

    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final CarRentalRepository carRentalRepository;


    @Override
    @Transactional
    public void createCarRental(Long customerId, RentalRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        if (request.getPickupDate() == null || request.getReturnDate() == null
                || !request.getPickupDate().isBefore(request.getReturnDate())) {
            throw new RuntimeException("Ngày nhận xe phải trước ngày trả xe");
        }

        long days = Math.max(1, Duration.between(request.getPickupDate(), request.getReturnDate()).toDays());

        for (Long carId : request.getCarIds()) {
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> new RuntimeException("Xe không tồn tại: " + carId));

            if (!"Available".equals(car.getStatus())) {
                throw new RuntimeException("Xe " + car.getCarName() + " hiện không sẵn sàng cho thuê");
            }

            if (carRentalRepository.existsOverlappingRental(
                    carId, request.getPickupDate(), request.getReturnDate())) {
                throw new RuntimeException("Xe " + car.getCarName()
                        + " đã có lịch thuê trùng khoảng thời gian đã chọn");
            }

            CarRental carRental = new CarRental();
            carRental.setCustomer(customer);
            carRental.setCar(car);
            carRental.setPickUpDate(request.getPickupDate());
            carRental.setReturnDate(request.getReturnDate());
            carRental.setRentPrice(car.getRentPrice().multiply(BigDecimal.valueOf(days)));
            carRental.setStatus("Pending");
            carRentalRepository.save(carRental);
        }
    }

    @Override
    public List<CarRental> findAll() {
        return carRentalRepository.findAll();
    }

    @Override
    public List<CarRental> findByCustomerId(Long customerId) {
        return carRentalRepository.findByCustomerId(customerId);
    }

    @Override
    public CarRental findById(Long id) {
        return carRentalRepository.findById(id).orElse(null);
    }

    private static final Set<String> VALID_STATUSES = Set.of("Pending", "Renting", "Completed", "Cancelled");

    @Override
    @Transactional
    public void updateStatus(Long rentalId, String status) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        CarRental carRental = carRentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Giao dịch thuê không tồn tại"));
        String currentStatus = carRental.getStatus();

        if("Cancelled".equals(currentStatus) || "Completed".equals(currentStatus)){
            throw new RuntimeException("Không thể đổi trạng thái giao dịch đã kết thúc");
        }

        if ("Pending".equals(currentStatus) && !"Renting".equals(status) && !"Cancelled".equals(status)) {
            throw new RuntimeException("Giao dịch đang chờ chỉ có thể chuyển sang Renting hoặc Cancelled");
        }

        if ("Renting".equals(currentStatus)
                && !"Completed".equals(status) && !"Cancelled".equals(status)) {
            throw new RuntimeException("Giao dịch đang thuê chỉ có thể chuyển sang Completed hoặc Cancelled");
        }

        carRental.setStatus(status);

        Car car = carRental.getCar();
        if ("Renting".equals(status)) {
            car.setStatus("Rented");
            carRepository.save(car);
        } else if ("Completed".equals(status) || "Cancelled".equals(status)) {
            if (!carRentalRepository.existsOtherActiveRental(car.getCarId(), carRental.getCarRentID())) {
                car.setStatus("Available");
                carRepository.save(car);
            }
        }
        carRentalRepository.save(carRental);
    }

    @Override
    public List<CarRental> findByPickUpDateBetween(LocalDateTime start, LocalDateTime end) {
        return carRentalRepository.findCarRentalByPickUpDate(start, end);
    }

    @Override
    public List<RentalReportDTO> findRentalReportByPickUpDateBetween(LocalDateTime start, LocalDateTime end) {
        return carRentalRepository.findCarRentalByPickUpDate(start, end)
                .stream()
                .map(this::toReportDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RentalReportDTO> findRentalReportPaginated(LocalDateTime start, LocalDateTime end, String status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("rentPrice").descending());
        String cleanStatus = cleanText(status);
        String cleanKeyword = cleanText(keyword);
        return carRentalRepository.findReportFiltered(start, end, cleanStatus, cleanKeyword, pageable).map(this::toReportDTO);
    }

    @Override
    public BigDecimal sumRentPriceFiltered(LocalDateTime start, LocalDateTime end, String status, String keyword) {
        BigDecimal total = carRentalRepository.sumRentPriceFiltered(start, end, cleanText(status), cleanText(keyword));
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Map<String, Long> countByStatusFiltered(LocalDateTime start, LocalDateTime end, String status, String keyword) {
        Map<String, Long> result = new HashMap<>();
        result.put("Pending", 0L);
        result.put("Renting", 0L);
        result.put("Completed", 0L);
        result.put("Cancelled", 0L);
        for (Object[] row : carRentalRepository.countGroupByStatusFiltered(start, end, cleanText(status), cleanText(keyword))) {
            if (row[0] != null && row[1] != null) {
                result.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
            }
        }
        return result;
    }

    private String cleanText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private RentalReportDTO toReportDTO(CarRental r) {
        return new RentalReportDTO(
                r.getCarRentID(),
                r.getCustomer() != null ? r.getCustomer().getFullName() : "—",
                r.getCar() != null ? r.getCar().getCarName() : "—",
                r.getPickUpDate(),
                r.getReturnDate(),
                r.getRentPrice(),
                r.getStatus());
    }

    @Override
    public Page<CarRental> findRentalsPaginated(Long customerId, String status, LocalDateTime start, LocalDateTime end, int page, int size, String sortBy, String sortDir) {
        String safeSortBy = switch (sortBy == null ? "" : sortBy) {
            case "pickUpDate", "returnDate", "rentPrice", "status", "carRentID" -> sortBy;
            default -> "carRentID";
        };
        Sort sort = "desc".equalsIgnoreCase(sortDir)
                ? Sort.by(safeSortBy).descending()
                : Sort.by(safeSortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String cleanStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;
        return carRentalRepository.findRentalsWithFilters(customerId, cleanStatus, start, end, pageable);
    }
}
