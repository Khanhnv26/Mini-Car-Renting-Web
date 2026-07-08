package com.assignment.carrentingsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "CarProducer")
public class CarProducer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProducerID")
    private Long producerId;

    @Column(name = "ProducerName", length = 255, nullable = false)
    private String producerName;

    @Column(name = "Address", length = 255, nullable = false)
    private String address;

    @Column(name = "Country", length = 255, nullable = false)
    private String country;

    @OneToMany(mappedBy = "carProducer")
    private List<Car> cars;
}
