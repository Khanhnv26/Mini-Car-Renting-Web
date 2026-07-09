package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.CarRentalDTO;
import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.entity.CarRental;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.CarRepository;
import com.assignment.carrentingsystem.repository.CustomerRepository;
import com.assignment.carrentingsystem.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarRentalServiceImpl implements CarRentalService {

    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final CarRentalRepository carRentalRepository;


    @Override
    @Transactional
    public void createCarRental(Long customerId, CarRentalDTO carRentalDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        Car car = carRepository.findById(carRentalDTO.getCarId())
                .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));

        if (!"Available".equals(car.getStatus())){
            throw new RuntimeException("Xe hiện không sẵn sàng cho thuê");
        }

        long days = Math.max(1, Duration.between(carRentalDTO.getPickupDate(), carRentalDTO.getReturnDate()).toDays());

        CarRental carRental = new CarRental();
        carRental.setCustomer(customer);
        carRental.setCar(car);
        carRental.setPickUpDate(carRentalDTO.getPickupDate());
        carRental.setReturnDate(carRentalDTO.getReturnDate());
        carRental.setRentPrice(car.getRentPrice().multiply(BigDecimal.valueOf(days)));
        carRental.setStatus("Pending");
        carRentalRepository.save(carRental);
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

        carRental.setStatus(status);

        Car car = carRental.getCar();
        if("Renting".equals(status)){
            car.setStatus("Rented");
            carRepository.save(car);
        } else if ("Completed".equals(status) || "Cancelled".equals(status)){
            car.setStatus("Available");
            carRepository.save(car);
        }
        carRentalRepository.save(carRental);
    }

    @Override
    public List<CarRental> findByPickUpDateBetween(LocalDateTime start, LocalDateTime end) {
        return carRentalRepository.findCarRentalByPickUpDate(start, end);
    }
}
