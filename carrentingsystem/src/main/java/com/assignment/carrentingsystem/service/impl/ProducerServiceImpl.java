package com.assignment.carrentingsystem.service.impl;

import com.assignment.carrentingsystem.dto.CarProducerDTO;
import com.assignment.carrentingsystem.entity.CarProducer;
import com.assignment.carrentingsystem.repository.CarProducerRepository;
import com.assignment.carrentingsystem.repository.CarRentalRepository;
import com.assignment.carrentingsystem.repository.CarRepository;
import com.assignment.carrentingsystem.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final CarProducerRepository carProducerRepository;
    private final CarRepository carRepository;

    private CarProducerDTO toDTO(CarProducer p) {
        CarProducerDTO dto = new CarProducerDTO();
        dto.setProducerId(p.getProducerId());
        dto.setProducerName(p.getProducerName());
        dto.setAddress(p.getAddress());
        dto.setCountry(p.getCountry());
        return dto;
    }
    private CarProducer toEntity(CarProducerDTO dto, CarProducer producer) {
        producer.setProducerName(dto.getProducerName());
        producer.setAddress(dto.getAddress());
        producer.setCountry(dto.getCountry());
        return producer;
    }

    @Override
    public List<CarProducer> findAll() {
        return carProducerRepository.findAll();
    }
    @Override
    public CarProducer findById(Long id) {
        return carProducerRepository.findById(id).orElse(null);
    }
    @Override
    public CarProducerDTO findDTOById(Long id) {
        CarProducer p = carProducerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hãng xe không tồn tại"));
        return toDTO(p);
    }
    @Override
    @Transactional
    public CarProducer save(CarProducerDTO carProducerDTO) {
        CarProducer producer;
        if (carProducerDTO.getProducerId() != null) {
            producer = carProducerRepository.findById(carProducerDTO.getProducerId())
                    .orElseThrow(() -> new RuntimeException("Hãng xe không tồn tại"));
        } else {
            producer = new CarProducer();
        }

        return carProducerRepository.save(toEntity(carProducerDTO, producer));
    }
    @Override
    @Transactional
    public void deleteById(Long id) {
        if (carRepository.existsByCarProducer_ProducerId(id)) {
            throw new RuntimeException("Không thể xoá hãng xe đang có xe");
        }
        carProducerRepository.deleteById(id);
    }
}
