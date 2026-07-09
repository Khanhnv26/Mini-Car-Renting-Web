package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.CarDTO;
import com.assignment.carrentingsystem.entity.Car;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CarService {
    List<Car> findAll();
    List<Car> findByStatus(String status);
    Car findById(Long id);
    Car save(CarDTO carDTO);
    void deleteById(Long id);
    CarDTO findDTOById(Long id);

}
