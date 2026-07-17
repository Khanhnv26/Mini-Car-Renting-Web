package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.RentalRequest;
import com.assignment.carrentingsystem.dto.CustomerDTO;
import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerCarController {

    private final CarService carService;
    private final CarRentalService carRentalService;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final ReviewService reviewService;
    private final ProducerService producerService;

    private Customer getCurrentCustomer(Authentication authentication) {
        String email = authentication.getName();
        Account account = accountService.findByEmail(email);
        return customerService.findByAccountId(account.getAccountId());
    }

    @GetMapping("/cars")
    public String listCars(
            @org.springframework.web.bind.annotation.RequestParam(name = "name", required = false) String name,
            @org.springframework.web.bind.annotation.RequestParam(name = "producerId", required = false) Long producerId,
            @org.springframework.web.bind.annotation.RequestParam(name = "minPrice", required = false) java.math.BigDecimal minPrice,
            @org.springframework.web.bind.annotation.RequestParam(name = "maxPrice", required = false) java.math.BigDecimal maxPrice,
            @org.springframework.web.bind.annotation.RequestParam(name = "page", defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(name = "sortBy", defaultValue = "carId") String sortBy,
            @org.springframework.web.bind.annotation.RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        org.springframework.data.domain.Page<com.assignment.carrentingsystem.entity.Car> carPage = 
            carService.findPaginated(name, producerId, "Available", minPrice, maxPrice, page, 5, sortBy, sortDir);
        model.addAttribute("cars", carPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("totalItems", carPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("producers", producerService.findAll());
        model.addAttribute("name", name);
        model.addAttribute("selectedProducerId", producerId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "customer/car-available";
    }

    @GetMapping("/rent")
    public String rentForm(Model model) {
        model.addAttribute("rentalRequest", new RentalRequest());
        model.addAttribute("cars", carService.findByStatus("Available"));
        return "customer/rent-form";
    }

    @PostMapping("/rent")
    public String rent(@Valid @ModelAttribute("rentalRequest") RentalRequest request, BindingResult bindingResult, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("cars", carService.findByStatus("Available"));
            return "customer/rent-form";
        }

        try {
            Customer customer = getCurrentCustomer(authentication);
            carRentalService.createCarRental(customer.getCustomerId(), request);
            redirectAttributes.addFlashAttribute("toastMessage", "Đặt thuê xe thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/customer/history";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cars", carService.findByStatus("Available"));
            return "customer/rent-form";
        }
    }

    @GetMapping("/history")
    public String history(
            @org.springframework.web.bind.annotation.RequestParam(name = "status", required = false) String status,
            @org.springframework.web.bind.annotation.RequestParam(name = "startDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @org.springframework.web.bind.annotation.RequestParam(name = "endDate", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @org.springframework.web.bind.annotation.RequestParam(name = "page", defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(name = "sortBy", defaultValue = "carRentID") String sortBy,
            @org.springframework.web.bind.annotation.RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model,
            Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        java.time.LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : null;
        java.time.LocalDateTime end = (endDate != null) ? endDate.atTime(23, 59, 59) : null;
        org.springframework.data.domain.Page<com.assignment.carrentingsystem.entity.CarRental> rentalPage = 
            carRentalService.findRentalsPaginated(customer.getCustomerId(), status, start, end, page, 5, sortBy, sortDir);
        model.addAttribute("rentals", rentalPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalPage.getTotalPages());
        model.addAttribute("totalItems", rentalPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("reviewDTO", new ReviewDTO());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "customer/history";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        model.addAttribute("customer", customerService.findByEmail(authentication.getName()));
        return "customer/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        model.addAttribute("customer", customer);
        return "customer/profile-form";
    }


    @PostMapping("/profile/edit")
    public String saveProfile(@ModelAttribute("customer") Customer customer,
                              Authentication authentication, RedirectAttributes redirectAttributes) {
        if (customer.getFullName() == null || customer.getFullName().isBlank()) {
            redirectAttributes.addFlashAttribute("toastMessage", "Tên không được để trống");
            redirectAttributes.addFlashAttribute("toastType", "error");
            return "redirect:/customer/profile/edit";
        }
        customerService.updateProfile(authentication.getName(), customer);
        redirectAttributes.addFlashAttribute("toastMessage", "Cập nhật hồ sơ thành công!");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/customer/profile";
    }

    @PostMapping("/review")
    public String review(@Valid @ModelAttribute("reviewDTO") ReviewDTO reviewDTO, BindingResult bindingResult, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Customer customer = getCurrentCustomer(authentication);
            model.addAttribute("rentals", carRentalService.findByCustomerId(customer.getCustomerId()));
            return "customer/history";
        }

        try {
            reviewService.save(reviewDTO);
            redirectAttributes.addFlashAttribute("toastMessage", "Gửi đánh giá thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/customer/history";
        } catch (Exception e) {
            Customer customer = getCurrentCustomer(authentication);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("rentals", carRentalService.findByCustomerId(customer.getCustomerId()));
            return "customer/history";

        }
    }




}
