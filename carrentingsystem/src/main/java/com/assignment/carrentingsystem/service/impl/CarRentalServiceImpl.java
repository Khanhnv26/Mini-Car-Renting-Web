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

    @Override
    @Transactional
    public void updateStatus(Long rentalId, String status) {
        CarRental carRental = carRentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Giao dịch thuê không tồn tại"));
        String currentStatus = carRental.getStatus();

        if("Cancelled".equals(currentStatus) || "Completed".equals(currentStatus)){
            throw new RuntimeException("Không thể đổi trạng thái giao dịch đã kết thúc");
        }

        carRental.setStatus(status);

        Car car = carRental.getCar();
        if("Renting".equals(status)){
            car.setStatus("Rented");
        } else if ("Completed".equals(status) || "Cancelled".equals(status)){
            car.setStatus("Avaliable");
            carRepository.save(car);
        }
        carRentalRepository.save(carRental);
    }

    @Override
    public List<CarRental> findByPickUpDateBetween(LocalDateTime start, LocalDateTime end) {
        return carRentalRepository.findCarRentalByPickUpDate(start, end);
    }
}
