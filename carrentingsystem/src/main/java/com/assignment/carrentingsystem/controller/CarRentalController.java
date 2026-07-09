package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rentals")
public class CarRentalController {

    private final CarRentalService carRentalService;

    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("carRentals", carRentalService.findAll());
        return "rental/rental-list";
    }

    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        carRentalService.updateStatus(id, status);
        return "redirect:/admin/rentals";
    }

    @GetMapping("/report")
    public String reportPage() {
        return "rental/rental-report";
    }

    @PostMapping("/report")
    public String report(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                         @RequestParam("endDate")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                         Model model) {
        model.addAttribute("rentals",carRentalService.findByPickUpDateBetween(startDate.atStartOfDay(),endDate.atTime(23,59,59)));
        model.addAttribute("startDate",startDate);
        model.addAttribute("endDate",endDate);
        return "rental/rental-report";
    }
}
