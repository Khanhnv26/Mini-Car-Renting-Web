package com.assignment.carrentingsystem.controller;

import com.assignment.carrentingsystem.dto.ProfileForm;
import com.assignment.carrentingsystem.dto.RentalRequest;
import com.assignment.carrentingsystem.dto.ReviewDTO;
import com.assignment.carrentingsystem.entity.Account;
import com.assignment.carrentingsystem.entity.Car;
import com.assignment.carrentingsystem.entity.CarRental;
import com.assignment.carrentingsystem.entity.Customer;
import com.assignment.carrentingsystem.entity.Review;
import com.assignment.carrentingsystem.service.CarRentalService;
import com.assignment.carrentingsystem.service.CarService;
import com.assignment.carrentingsystem.service.CustomerService;
import com.assignment.carrentingsystem.service.ProducerService;
import com.assignment.carrentingsystem.service.ReviewService;
import com.assignment.carrentingsystem.util.DateRangeRules;
import com.assignment.carrentingsystem.util.PriceRangeRules;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerCarController {

    private final CarService carService;
    private final CarRentalService carRentalService;
    private final CustomerService customerService;
    private final ReviewService reviewService;
    private final ProducerService producerService;

    private Account currentUser(HttpSession session) {
        return (Account) session.getAttribute("currentUser");
    }

    private Customer getCurrentCustomer(HttpSession session) {
        Account account = currentUser(session);
        return customerService.findByAccountId(account.getAccountId());
    }

    @GetMapping("/cars")
    public String listCars(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "producerId", required = false) Integer producerId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "carName") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("producers", producerService.findAll());
        model.addAttribute("name", name);
        model.addAttribute("selectedProducerId", producerId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        String filterError = PriceRangeRules.validateOptionalRange(minPrice, maxPrice);
        if (filterError != null) {
            model.addAttribute("toastMessage", filterError);
            model.addAttribute("toastType", "error");
            model.addAttribute("cars", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            return "customer/car-available";
        }

        Page<Car> carPage = carService.findPaginated(name, producerId, "Available", minPrice, maxPrice, page, 6, sortBy, sortDir);
        model.addAttribute("cars", carPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carPage.getTotalPages());
        model.addAttribute("totalItems", carPage.getTotalElements());
        return "customer/car-available";
    }

    private void addCarReviewModel(Model model, Integer carId) {
        if (carId == null) {
            return;
        }
        Car selectedCar = carService.findById(carId);
        if (selectedCar == null) {
            return;
        }
        List<Review> carReviews = reviewService.findByCarId(carId);
        double avgStar = carReviews.stream()
                .mapToInt(Review::getReviewStar)
                .average()
                .orElse(0);
        model.addAttribute("selectedCar", selectedCar);
        model.addAttribute("carReviews", carReviews);
        model.addAttribute("avgStar", avgStar);
        model.addAttribute("avgStarRounded", (int) Math.round(avgStar));
        model.addAttribute("reviewCount", carReviews.size());
    }

    @GetMapping("/cars/{carId}/reviews")
    public String carReviews(@PathVariable("carId") Integer carId,
                             @RequestParam(name = "returnCarIds", required = false) List<Integer> returnCarIds,
                             Model model) {
        Car car = carService.findById(carId);
        if (car == null) {
            return "redirect:/customer/cars";
        }
        addCarReviewModel(model, carId);
        List<Integer> backIds = returnCarIds == null
                ? List.of()
                : returnCarIds.stream().filter(Objects::nonNull).toList();
        model.addAttribute("returnCarIds", backIds);
        return "customer/car-reviews";
    }

    private void addRentFormModel(Model model) {
        model.addAttribute("cars", carService.findByStatus("Available"));
        model.addAttribute("today", LocalDate.now());
    }

    @GetMapping("/rent")
    public String rentForm(@RequestParam(name = "carId", required = false) Integer carId,
                           @RequestParam(name = "carIds", required = false) List<Integer> carIds,
                           Model model) {
        RentalRequest req = new RentalRequest();
        List<Integer> selected = new ArrayList<>();
        if (carIds != null) {
            selected.addAll(carIds.stream().filter(Objects::nonNull).toList());
        }
        if (carId != null && !selected.contains(carId)) {
            selected.add(0, carId);
        }
        if (!selected.isEmpty()) {
            req.setCarIds(selected);
        }
        model.addAttribute("rentalRequest", req);
        addRentFormModel(model);
        return "customer/rent-form";
    }

    @PostMapping("/rent")
    public String rent(@Valid @ModelAttribute("rentalRequest") RentalRequest request,
                       BindingResult bindingResult,
                       Model model,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addRentFormModel(model);
            return "customer/rent-form";
        }

        try {
            Customer customer = getCurrentCustomer(session);
            carRentalService.createCarRental(customer.getCustomerId(), request);
            redirectAttributes.addFlashAttribute("toastMessage", "Đặt thuê xe thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/customer/history";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            addRentFormModel(model);
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
            @RequestParam(name = "sortBy", defaultValue = "pickupDate") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model,
            HttpSession session) {
        Customer customer = getCurrentCustomer(session);
        int pageSize = 5;
        model.addAttribute("selectedStatus", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("pageSize", pageSize);
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewStar(5);
        model.addAttribute("reviewDTO", reviewDTO);

        String filterError = DateRangeRules.validateOptionalRange(startDate, endDate);
        if (filterError != null) {
            model.addAttribute("toastMessage", filterError);
            model.addAttribute("toastType", "error");
            model.addAttribute("rentals", List.of());
            model.addAttribute("reviewedRentalIds", java.util.Set.of());
            model.addAttribute("reviewsByRentalId", Map.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            return "customer/history";
        }

        Page<CarRental> rentalPage = carRentalService.findRentalsPaginated(
                customer.getCustomerId(), status, startDate, endDate, null, page, pageSize, sortBy, sortDir);
        List<Integer> rentalIds = rentalPage.getContent().stream()
                .map(CarRental::getCarRenId)
                .toList();
        Map<Integer, Review> reviewsByRentalId = reviewService.findByCarRentalIds(rentalIds);
        model.addAttribute("rentals", rentalPage.getContent());
        model.addAttribute("reviewedRentalIds", reviewsByRentalId.keySet());
        model.addAttribute("reviewsByRentalId", reviewsByRentalId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalPage.getTotalPages());
        model.addAttribute("totalItems", rentalPage.getTotalElements());
        return "customer/history";
    }

    @GetMapping("/reviews")
    public String myReviews(
            @RequestParam(name = "star", required = false) Integer star,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model,
            HttpSession session) {
        Customer customer = getCurrentCustomer(session);
        Page<Review> reviewPage = reviewService.findByCustomerFiltered(
                customer.getCustomerId(), star, keyword, page, 6);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("totalItems", reviewPage.getTotalElements());
        model.addAttribute("selectedStar", star);
        model.addAttribute("keyword", keyword);
        return "customer/my-reviews";
    }

    private void addProfileDateBounds(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        model.addAttribute("maxBirthday", today.minusYears(18));
    }

    private ProfileForm toProfileForm(Customer customer) {
        ProfileForm form = new ProfileForm();
        form.setFullName(customer.getFullName());
        form.setMobile(customer.getMobile());
        form.setBirthday(customer.getBirthday());
        form.setIdentityCard(customer.getIdentityCard());
        form.setLicenceNumber(customer.getLicenceNumber());
        form.setLicenceDate(customer.getLicenceDate());
        return form;
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        model.addAttribute("customer", getCurrentCustomer(session));
        return "customer/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, HttpSession session) {
        Customer customer = getCurrentCustomer(session);
        model.addAttribute("profileForm", toProfileForm(customer));
        addProfileDateBounds(model);
        return "customer/profile-form";
    }

    @PostMapping("/profile/edit")
    public String saveProfile(@Valid @ModelAttribute("profileForm") ProfileForm profileForm,
                              BindingResult bindingResult,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addProfileDateBounds(model);
            return "customer/profile-form";
        }
        try {
            Customer data = new Customer();
            data.setFullName(profileForm.getFullName());
            data.setMobile(profileForm.getMobile());
            data.setBirthday(profileForm.getBirthday());
            data.setIdentityCard(profileForm.getIdentityCard());
            data.setLicenceNumber(profileForm.getLicenceNumber());
            data.setLicenceDate(profileForm.getLicenceDate());
            customerService.updateProfile(currentUser(session).getEmail(), data);
            redirectAttributes.addFlashAttribute("toastMessage", "Cập nhật hồ sơ thành công!");
            redirectAttributes.addFlashAttribute("toastType", "success");
            return "redirect:/customer/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            addProfileDateBounds(model);
            return "customer/profile-form";
        }
    }

    @PostMapping("/review")
    public String review(@Valid @ModelAttribute("reviewDTO") ReviewDTO reviewDTO,
                         BindingResult bindingResult,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer(session);
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
