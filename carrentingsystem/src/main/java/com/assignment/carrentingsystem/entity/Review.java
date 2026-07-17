package com.assignment.carrentingsystem.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CarRentID", nullable = false, unique = true)
    private CarRental carRental;

    @Column(name = "ReviewStar", nullable = false)
    private Integer reviewStar;

    @Column(name = "Comment", nullable = false, length = 500)
    private String comment;



}
