package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.CarDTO;
import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.entity.CarProducer;
import com.assignment.carrentingsystem.repository.CarProducerRepository;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.CarRepository;
import com.assignment.carrentingsystem.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarRentalRepository carRentalRepository;
    private final CarProducerRepository carProducerRepository;

    private CarDTO toDTO(Car car) {
        CarDTO carDTO = new CarDTO();
        carDTO.setCarId(car.getCarId());
        carDTO.setCarName(car.getCarName());
        carDTO.setCarModelYear(car.getCarModelYear());
        carDTO.setColor(car.getColor());
        carDTO.setCapacity(car.getCapacity());
        carDTO.setDescription(car.getDescription());
        carDTO.setImportDate(car.getImportDate());
        carDTO.setRentPrice(car.getRentPrice());
        carDTO.setStatus(car.getStatus());
        carDTO.setProducerId(car.getCarProducer().getProducerId());
        return carDTO;
    }

    private Car toEntity(CarDTO carDTO, Car car) {
        CarProducer carProducer = carProducerRepository.findById(carDTO.getProducerId())
                .orElseThrow(() -> new RuntimeException("Hãng xe không tồn tại"));

        car.setCarName(carDTO.getCarName());
        car.setCarModelYear(carDTO.getCarModelYear());
        car.setColor(carDTO.getColor());
        car.setCapacity(carDTO.getCapacity());
        car.setDescription(carDTO.getDescription());
        car.setImportDate(carDTO.getImportDate());
        car.setRentPrice(carDTO.getRentPrice());
        car.setStatus(carDTO.getStatus());
        car.setCarProducer(carProducer);

        return car;
    }

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public List<Car> findByStatus(String status) {
        return carRepository.findByStatus(status);
    }

    @Override
    public Car findById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Car save(CarDTO carDTO) {
        Car car;
        if (carDTO.getCarId() != null) {
            car = carRepository.findById(carDTO.getCarId())
                    .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));
        } else {
            car = new Car();
        }
        return carRepository.save(toEntity(carDTO, car));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (carRentalRepository.existsByCar_CarId(id)) {
            Car car = carRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Xe không tồn tại"));
            car.setStatus("Inactive");
            carRepository.save(car);
        } else {
            carRepository.deleteById(id);
        }
    }

    @Override
    public CarDTO findDTOById(Long id) {
        Car car = carRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Xe không tồn tại"));
        return toDTO(car);
    }
}
