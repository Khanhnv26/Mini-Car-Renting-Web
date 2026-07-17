package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.RentalRequest;
import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.entity.CarRental;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.entity.Review;
import com.assignment.carrentingsystem.service.AccountService;
import com.assignment.carrentingsystem.service.CarRentalService;
import com.assignment.carrentingsystem.service.CarService;
import com.assignment.carrentingsystem.service.CustomerService;
import com.assignment.carrentingsystem.service.ProducerService;
import com.assignment.carrentingsystem.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "producerId", required = false) Long producerId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "carId") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        Page<Car> carPage = carService.findPaginated(
                name, producerId, "Available", minPrice, maxPrice, page, 6, sortBy, sortDir);
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
    public String rentForm(@RequestParam(name = "carId", required = false) Long carId, Model model) {
        RentalRequest req = new RentalRequest();
        if (carId != null) {
            req.setCarIds(List.of(carId));
        }
        model.addAttribute("rentalRequest", req);
        model.addAttribute("cars", carService.findByStatus("Available"));
        return "customer/rent-form";
    }

    @PostMapping("/rent")
    public String rent(@Valid @ModelAttribute("rentalRequest") RentalRequest request,
                       BindingResult bindingResult,
                       Model model,
                       Authentication authentication,
                       RedirectAttributes redirectAttributes) {
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
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "carRentID") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model,
            Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime end = (endDate != null) ? endDate.atTime(23, 59, 59) : null;
        Page<CarRental> rentalPage = carRentalService.findRentalsPaginated(
                customer.getCustomerId(), status, start, end, page, 5, sortBy, sortDir);
        List<Long> rentalIds = rentalPage.getContent().stream()
                .map(CarRental::getCarRentID)
                .toList();
        Map<Long, Review> reviewsByRentalId = reviewService.findByCarRentalIds(rentalIds);
        model.addAttribute("rentals", rentalPage.getContent());
        model.addAttribute("reviewedRentalIds", reviewsByRentalId.keySet());
        model.addAttribute("reviewsByRentalId", reviewsByRentalId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalPage.getTotalPages());
        model.addAttribute("totalItems", rentalPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewStar(5);
        model.addAttribute("reviewDTO", reviewDTO);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "customer/history";
    }

    @GetMapping("/reviews")
    public String myReviews(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model,
            Authentication authentication) {
        Customer customer = getCurrentCustomer(authentication);
        Page<Review> reviewPage = reviewService.findByCustomerIdPaginated(customer.getCustomerId(), page, 6);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("totalItems", reviewPage.getTotalElements());
        return "customer/my-reviews";
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
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
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
    public String review(@Valid @ModelAttribute("reviewDTO") ReviewDTO reviewDTO,
                         BindingResult bindingResult,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer(authentication);
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .filter(m -> m != null && !m.isBlank())
                    .findFirst()
                    .orElse("Dữ liệu đánh giá không hợp lệ");
            redirectAttributes.addFlashAttribute("toastMessage", msg);
            redirectAttributes.addFlashAttribute("toastType", "error");
            return "redirect:/customer/history";
        }

        try {
            reviewService.save(reviewDTO, customer.getCustomerId());
            redirectAttributes.addFlashAttribute("toastMessage", "Gửi đánh giá thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/customer/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
            return "redirect:/customer/history";
        }
    }
}
