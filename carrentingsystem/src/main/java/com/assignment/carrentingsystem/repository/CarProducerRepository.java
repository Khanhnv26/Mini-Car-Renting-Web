package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.CarProducer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarProducerRepository extends JpaRepository<CarProducer, Long> {

}
