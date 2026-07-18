package com.assignment.carrentingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Integer producerId;

    @Column(name = "ProducerName", length = 100, nullable = false)
    private String producerName;

    @Column(name = "Address", length = 200, nullable = false)
    private String address;

    @Column(name = "Country", length = 100, nullable = false)
    private String country;

    @OneToMany(mappedBy = "carProducer")
    private List<Car> cars;
}
