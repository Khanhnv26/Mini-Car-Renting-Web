package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.CarProducerDTO;
import com.assignment.carrentingsystem.entity.CarProducer;
import com.assignment.carrentingsystem.repository.CarProducerRepository;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.CarRepository;
import com.assignment.carrentingsystem.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final CarProducerRepository carProducerRepository;
    private final CarRepository carRepository;

    @Override
    public List<CarProducer> findAll() {
        return carProducerRepository.findAll();
    }

    @Override
    public CarProducer findById(Long id) {
        return carProducerRepository.findById(id).orElse(null);
    }

    @Override
    public CarProducer save(CarProducerDTO carProducerDTO) {
       CarProducer carProducer;
       if(carProducerDTO.getProducerId() != null){
           carProducer = carProducerRepository.findById(carProducerDTO.getProducerId())
                   .orElseThrow(() -> new RuntimeException("Hãng xe không tồn tại"));
       } else {
           carProducer = new CarProducer();
       }
       carProducer.setProducerName(carProducerDTO.getProducerName());
       carProducer.setAddress(carProducerDTO.getAddress());
       carProducer.setCountry(carProducerDTO.getCountry());

       return carProducerRepository.save(carProducer);
    }

    @Override
    public void deleteById(Long id) {
        if(carRepository.existsByCarProducer_ProducerId(id)){
            throw new RuntimeException("Không thể xoá hãng xe đang có xe");
        }
        carProducerRepository.deleteById(id);
    }
}
