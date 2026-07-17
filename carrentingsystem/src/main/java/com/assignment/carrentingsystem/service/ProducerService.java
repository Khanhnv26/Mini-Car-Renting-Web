package com.assignment.carrentingsystem.service;

import com.assignment.carrentingsystem.dto.CarProducerDTO;
import com.assignment.carrentingsystem.entity.CarProducer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProducerService {
    List<CarProducer> findAll();
    CarProducer findById(Long id);
    CarProducerDTO findDTOById(Long id);
    CarProducer save(CarProducerDTO carProducerDTO);
    void deleteById(Long id);
    Page<CarProducer> findPaginated(String keyword, String country, int page, int size, String sortBy, String sortDir);
    List<String> findCountries();
}
