package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.CarProducerDTO;
import com.assignment.carrentingsystem.entity.CarProducer;

import java.util.List;

public interface ProducerService {
    List<CarProducer> findAll();
    CarProducer findById(Long id);
    CarProducer save(CarProducerDTO carProducerDTO);
    void deleteById(Long id);
}
