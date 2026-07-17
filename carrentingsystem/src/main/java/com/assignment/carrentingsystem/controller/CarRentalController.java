package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.RentalReportDTO;
import com.assignment.carrentingsystem.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rentals")
public class CarRentalController {

    private final CarRentalService carRentalService;

    @GetMapping
    public String listAll(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "carRentID") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        String filterStatus;
        String selectedStatus;
        if (status == null) {
            filterStatus = "Pending";
            selectedStatus = "Pending";
        } else if (status.isBlank()) {
            filterStatus = null;
            selectedStatus = "";
        } else {
            filterStatus = status.trim();
            selectedStatus = filterStatus;
        }

        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime end = (endDate != null) ? endDate.atTime(23, 59, 59) : null;

        var rentalPage = carRentalService.findRentalsPaginated(
                null, filterStatus, start, end, page, 5, sortBy, sortDir);

        model.addAttribute("carRentals", rentalPage.getContent());
        model.addAttribute("rentals", rentalPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalPage.getTotalPages());
        model.addAttribute("totalItems", rentalPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
        model.addAttribute("selectedStatus", selectedStatus);
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
    public String report(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model) {

        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime end = (endDate != null) ? endDate.atTime(23, 59, 59) : null;
        String cleanStatus = (status != null && !status.isBlank()) ? status.trim() : null;
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        Page<RentalReportDTO> reportPage = carRentalService.findRentalReportPaginated(
                start, end, cleanStatus, cleanKeyword, page, 10);
        BigDecimal totalRevenue = carRentalService.sumRentPriceFiltered(start, end, cleanStatus, cleanKeyword);
        long totalItems = reportPage.getTotalElements();
        BigDecimal averageRevenue = totalItems > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalItems), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        var statusCounts = carRentalService.countByStatusFiltered(start, end, cleanStatus, cleanKeyword);

        model.addAttribute("rentals", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reportPage.getTotalPages());
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedStatus", cleanStatus != null ? cleanStatus : "");
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("averageRevenue", averageRevenue);
        model.addAttribute("pendingCount", statusCounts.getOrDefault("Pending", 0L));
        model.addAttribute("rentingCount", statusCounts.getOrDefault("Renting", 0L));
        model.addAttribute("completedCount", statusCounts.getOrDefault("Completed", 0L));
        model.addAttribute("cancelledCount", statusCounts.getOrDefault("Cancelled", 0L));
        return "rental/rental-report";
    }
}
