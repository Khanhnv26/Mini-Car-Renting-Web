package com.assignment.carrentingsystem.repository;

import com.assignment.carrentingsystem.entity.CarProducer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarProducerRepository extends JpaRepository<CarProducer, Integer> {

    @Query("SELECT p FROM CarProducer p WHERE " +
           "(:keyword IS NULL OR LOWER(p.producerName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.country) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:country IS NULL OR p.country = :country)")
    Page<CarProducer> findWithFilters(@Param("keyword") String keyword,
                                      @Param("country") String country,
                                      Pageable pageable);

    @Query("SELECT DISTINCT p.country FROM CarProducer p ORDER BY p.country")
    List<String> findDistinctCountries();
}
