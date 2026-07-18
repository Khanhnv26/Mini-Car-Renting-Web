package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.RentalReportDTO;
import com.assignment.carrentingsystem.service.CarRentalService;
import com.assignment.carrentingsystem.util.DateRangeRules;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

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
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "pickupDate") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        String filterStatus = null;
        String selectedStatus = "";
        if (status != null && !status.isBlank()) {
            filterStatus = status.trim();
            selectedStatus = filterStatus;
        }

        int pageSize = 5;
        String filterError = DateRangeRules.validateOptionalRange(startDate, endDate);
        model.addAttribute("selectedStatus", selectedStatus);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equals(sortDir) ? "desc" : "asc");
        model.addAttribute("pageSize", pageSize);

        if (filterError != null) {
            model.addAttribute("toastMessage", filterError);
            model.addAttribute("toastType", "error");
            model.addAttribute("carRentals", List.of());
            model.addAttribute("rentals", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            return "rental/rental-list";
        }

        var rentalPage = carRentalService.findRentalsPaginated(
                null, filterStatus, startDate, endDate, keyword, page, pageSize, sortBy, sortDir);

        model.addAttribute("carRentals", rentalPage.getContent());
        model.addAttribute("rentals", rentalPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalPage.getTotalPages());
        model.addAttribute("totalItems", rentalPage.getTotalElements());
        return "rental/rental-list";
    }

    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable("id") Integer id, @RequestParam("status") String status, RedirectAttributes redirectAttributes) {
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

        String cleanStatus = (status != null && !status.isBlank()) ? status.trim() : null;
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        int pageSize = 10;

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedStatus", cleanStatus != null ? cleanStatus : "");
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageSize", pageSize);

        String filterError = DateRangeRules.validateOptionalRange(startDate, endDate);
        if (filterError != null) {
            model.addAttribute("toastMessage", filterError);
            model.addAttribute("toastType", "error");
            model.addAttribute("rentals", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
            model.addAttribute("averageRevenue", BigDecimal.ZERO);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("rentingCount", 0L);
            model.addAttribute("completedCount", 0L);
            model.addAttribute("cancelledCount", 0L);
            return "rental/rental-report";
        }

        Page<RentalReportDTO> reportPage = carRentalService.findRentalReportPaginated(
                startDate, endDate, cleanStatus, cleanKeyword, page, pageSize);

        BigDecimal totalRevenue = carRentalService.sumRentPriceFiltered(startDate, endDate, "Completed", cleanKeyword);
        var statusCounts = carRentalService.countByStatusFiltered(startDate, endDate, null, cleanKeyword);
        long completedCount = statusCounts.getOrDefault("Completed", 0L);
        BigDecimal averageRevenue = completedCount > 0
                ? totalRevenue.divide(BigDecimal.valueOf(completedCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        model.addAttribute("rentals", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reportPage.getTotalPages());
        model.addAttribute("totalItems", reportPage.getTotalElements());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("averageRevenue", averageRevenue);
        model.addAttribute("pendingCount", statusCounts.getOrDefault("Pending", 0L));
        model.addAttribute("rentingCount", statusCounts.getOrDefault("Renting", 0L));
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("cancelledCount", statusCounts.getOrDefault("Cancelled", 0L));
        return "rental/rental-report";
    }
}
