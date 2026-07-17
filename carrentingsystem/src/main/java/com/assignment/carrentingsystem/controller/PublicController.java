package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.entity.Review;
import com.assignment.carrentingsystem.service.CarService;
import com.assignment.carrentingsystem.service.ProducerService;
import com.assignment.carrentingsystem.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class PublicController {

    private final CarService carService;
    private final ProducerService producerService;
    private final ReviewService reviewService;

    @GetMapping("/cars")
    public String cars(
            Model model,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "producerId", required = false) Long producerId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "pickupDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate pickupDate,
            @RequestParam(name = "returnDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "rentPrice") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {

        Page<Car> carPage = carService.findPaginated(
                name, producerId, "Available", minPrice, maxPrice, page, 6, sortBy, sortDir);

        model.addAttribute("cars", carPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("totalItems", carPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
        model.addAttribute("producers", producerService.findAll());
        model.addAttribute("name", name);
        model.addAttribute("selectedProducerId", producerId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("pickupDate", pickupDate);
        model.addAttribute("returnDate", returnDate);
        return "public/cars";
    }

    @GetMapping("/reviews")
    public String reviews(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<Review> reviewPage = reviewService.findAllPaginated(page, 6);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("totalItems", reviewPage.getTotalElements());
        return "public/reviews";
    }
}
