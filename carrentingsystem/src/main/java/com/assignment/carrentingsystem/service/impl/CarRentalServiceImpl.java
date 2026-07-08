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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarRentalServiceImpl implements CarRentalService {

    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final CarRentalRepository carRentalRepository;


    @Override
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
        return List.of();
    }

    @Override
    public List<CarRental> findByCustomerId(Long customerId) {
        return List.of();
    }

    @Override
    public CarRental findById(Long id) {
        return null;
    }

    @Override
    public void updateStatus(Long rentalId, String status) {

    }

    @Override
    public List<CarRental> findByPickUpDateBetween(LocalDateTime start, LocalDateTime end) {
        return List.of();
    }
}
