package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rentals")
public class CarRentalController {

    private final CarRentalService carRentalService;

    @GetMapping
    public String listAll(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "carRentID") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        java.time.LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : null;
        java.time.LocalDateTime end = (endDate != null) ? endDate.atTime(23, 59, 59) : null;
        org.springframework.data.domain.Page<com.assignment.carrentingsystem.entity.CarRental> rentalPage = 
            carRentalService.findRentalsPaginated(null, status, start, end, page, 5, sortBy, sortDir);
        model.addAttribute("carRentals", rentalPage.getContent());
        model.addAttribute("rentals", rentalPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalPage.getTotalPages());
        model.addAttribute("totalItems", rentalPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "rental/rental-list";
    }

    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status, RedirectAttributes redirectAttributes) {
        try {
            carRentalService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("toastMessage", "Cập nhật trạng thái thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
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
        model.addAttribute("rentals",carRentalService.findRentalReportByPickUpDateBetween(startDate.atStartOfDay(),endDate.atTime(23,59,59)));
        model.addAttribute("startDate",startDate);
        model.addAttribute("endDate",endDate);
        return "rental/rental-report";
    }
}
