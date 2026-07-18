package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.CarDTO;
import com.assignment.carrentingsystem.entity.Car;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface CarService {
    List<Car> findAll();
    List<Car> findByStatus(String status);
    Car findById(Integer id);
    Car save(CarDTO carDTO);
    void deleteById(Integer id);
    CarDTO findDTOById(Integer id);
    Page<Car> findPaginated(String name, Integer producerId, String status, BigDecimal minPrice, BigDecimal maxPrice, int page, int size, String sortBy, String sortDir);
}
